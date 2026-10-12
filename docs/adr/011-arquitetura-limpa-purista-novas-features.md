# [011]: [Arquitetura Limpa Purista para Novas Features]

* **Status**: Aceito
* **Data**: 30/05/2026

## 1. Contexto e Problema

O projeto evoluiu para uma separação mais rígida entre núcleo de negócio e detalhes de infraestrutura. Ao criar novas features, existe o risco de misturar validação HTTP, persistência, mensagens internacionalizadas e regras de negócio na mesma camada, o que reduz testabilidade, aumenta acoplamento e dificulta evolução.

## 2. Decisão

Adotamos a seguinte organização como padrão para novas features:

- **`core` é o centro da aplicação**:
  - contém entidades de domínio, value objects, regras de negócio e comportamentos;
  - entidades que necessitam de auditoria estendem `AuditDomain` e controlam timestamps explicitamente via `create()`, `update()` e `delete()`;
  - contém use cases que implementam `UseCase<I, O>` (com retorno) ou `VoidUseCase<I>` (sem retorno) e apenas orquestram fluxo;
  - contém portas (`ports`) para acesso a persistência com contratos descritivos baseados em intenção (ex.: `exists*` booleanos);
  - não depende de Spring Web, JPA, validação HTTP ou qualquer detalhe de infraestrutura.

- **`infra` é a borda técnica**:
  - contém controllers, DTOs de request/response, mappers, repositórios JPA, configurações e handlers globais;
  - converte entrada HTTP em comandos/queries do core;
  - converte exceções de domínio em respostas HTTP;
  - concentra validações específicas de transporte (ex.: `@NotBlank`, `@LicensePlate`, `@Valid`);
  - contém a configuração dos beans de use case.

- **Use cases recebem apenas tipos do core**:
  - comandos e queries vivem no pacote do core e são records imutáveis;
  - validação de formato (ex.: `@NotBlank`, `@Email`, `@NotNull`) fica nos DTOs de request da infraestrutura, não nos comandos/queries do core;
  - controllers fazem a tradução entre DTOs da API e comandos/queries do core.

- **Exceções seguem contrato de domínio**:
  - exceções de negócio devem estender `DomainExceptionCore`;
  - cada exceção centralizada no módulo deve carregar `messageKey` e tipo/status de erro compatível com o contrato do projeto;
  - erros inesperados/técnicos são representados por `TechnicalException` e tratados pela infraestrutura como `500 Internal Server Error`;
  - `TechnicalException` vive em `shared/exception` (fora do core) e é logada como erro no `GlobalExceptionHandler`.

- **Validação de formato fica na borda HTTP ou nos commands**:
  - validações como placa, e-mail, tamanho de senha e campos obrigatórios devem ficar em request DTOs da API ou em anotações Bean Validation nos commands do core;
  - o domínio deve validar apenas regras de negócio e consistência interna.

- **Auditoria e soft delete são controlados pelo domínio**:
  - timestamps de auditoria são gerenciados por chamadas explícitas (`create()`, `update()`, `delete()`) na entidade, não por annotations do Hibernate;
  - soft delete ocorre no domínio (entidade marca `deletedAt`) e o gateway persiste a mudança via `update()`, não via `@SQLDelete` ou `delete()` do repositório;
  - consultas no gateway filtram `deleted_at IS NULL` explicitamente.

## 3. Diretrizes de Estrutura para uma Nova Feature

Ao criar uma nova feature, seguir este fluxo:

### 3.1. Núcleo de domínio
- Criar a entidade principal em `core/<feature>/domain`.
- Colocar regras de negócio, invariantes e comportamentos dentro da própria entidade.
- Usar `DomainExceptionCore` para erros de negócio.

### 3.2. Contratos de entrada do caso de uso
- Criar `Commands` e `Queries` no pacote do core, por exemplo:
  - `core/<feature>/usecase/command`
  - `core/<feature>/usecase/query`
- Esses tipos não devem depender de DTOs da API nem de classes do Spring.

### 3.3. Portas do domínio
- Definir interfaces de persistência/integração em `core/<feature>/domain/port`.
- A interface do core deve descrever intenção, não implementação.
- **Prefira contratos descritivos baseados em intenção**: em vez de expor `Optional<Entity> findByXxx()` para consultas de unicidade, use métodos booleanos como `existsByXxx()` — o use case não precisa da entidade completa, apenas da resposta sim/não.
- Para consultas de unicidade com exclusão do próprio registro (ex.: update), use `existsByXxxAndIdNot(valor, id)`.
- O gateway **não** deve conter métodos de `delete` que aceitem entidades — o soft delete é disparado pelo domínio via `entidade.delete()`, e o gateway persiste com `update()`.
- Métodos do gateway que recebem entidades (create, update) devem validar nullidade internamente e lançar `TechnicalException` se o parâmetro for nulo.

### 3.4. Casos de uso
- Criar use cases no core para cada operação relevante.
- **Use cases com retorno devem implementar a interface `UseCase<I, O>`**:
  ```java
  public interface UseCase<I, O> {
      O execute(I input);
  }
  ```
  - `I` é o tipo do comando/query de entrada.
  - `O` é o tipo do retorno.

- **Use cases sem retorno (void) devem implementar a interface `VoidUseCase<I>`**:
  ```java
  public interface VoidUseCase<I> {
      void execute(I input);
  }
  ```
  - `I` é o tipo do comando/query de entrada.
  - Indicado para operações como delete, soft delete, update, ou comandos que não produzem valor de retorno.
- O use case deve:
  - validar fluxo de negócio (regras de consistência, unicidade via gateway, etc.);
  - chamar entidades e portas;
  - **não realizar validação de formato** (ex.: campos obrigatórios, formato de e-mail) — essa validação fica declarativamente no command via anotações `jakarta.validation`;
  - não conhecer controller, request, response, JPA ou HTTP.
- **Commands e Queries**:
  - São records imutáveis no pacote do core.
  - **Não contêm anotações `jakarta.validation`** — validação de formato fica nos DTOs de request da infraestrutura.
  - Validam apenas regras de negócio dentro do use case.
- **Configuração dos use cases**:
  - Os beans são registrados diretamente na configuração da infraestrutura, sem decoradores:
    ```java
    @Bean
    public CreateClientUseCase createClientUseCase(ClientGateway gateway) {
        return new CreateClientUseCase(gateway);
    }

    @Bean
    public SoftDeleteClientUseCase softDeleteClientUseCase(ClientGateway gateway) {
        return new SoftDeleteClientUseCase(gateway);
    }
    ```

### 3.5. Infraestrutura e adaptação HTTP
- Criar controller em `infra/features/<feature>/api`.
- Criar DTOs de request/response em `infra/features/<feature>/api/dto`.
- Mapear DTOs da API para commands/queries do core.
- Validar campos de entrada com Bean Validation na borda.

### 3.6. Persistência
- Criar entidade JPA em `infra/features/<feature>/persistence/entity`.
- Criar mapper entre domínio e entidade JPA.
- Criar implementação da porta do core em `infra/features/<feature>/persistence/jpa`.

### 3.7. Configuração
- Registrar os use cases em uma classe de configuração da feature, se necessário.
- Manter wiring, beans e dependências externas fora do core.

### 3.8. Tratamento de exceções
- Centralizar o tratamento em `GlobalExceptionHandler`.
- Traduzir exceções de domínio (`DomainExceptionCore`) para status HTTP adequados (422, 409, 404, etc.).
- **`TechnicalException`** (em `shared/exception`):
  - Usada para erros de infraestrutura, como entidade nula ao persistir ou falha inesperada no repositório.
  - Deve ser logada como `ERROR` com stack trace.
  - Resulta em `500 Internal Server Error` no `GlobalExceptionHandler`.
  - Carrega um `code` (message key) e `args` para internacionalização.
- Garantir que `500` fique reservado a falhas inesperadas/técnicas.
- **`ConstraintViolationException`** (da validação `jakarta.validation`) deve ser tratada no `GlobalExceptionHandler` com `400 Bad Request`, extraindo as mensagens de violação.

### 3.9. Auditoria e soft delete
- Entidades de domínio que necessitam de auditoria devem estender `AuditDomain` (em `shared/domain`).
- **Ciclo de vida explícito**: a entidade controla seus timestamps por métodos explícitos:
  - `create()` — define `dateCreated` e `dateUpdated`.
  - `update()` — atualiza `dateUpdated`.
  - `delete()` — define `deletedAt` e `dateUpdated`.
- **Factory method**: a criação da entidade é feita por um método estático `create(...)` que chama `new()` + `create()` + `validate()`.
- **Reconstituição**: para recriar a entidade a partir da persistência (JPA mapper), usar um método estático `restore(...)` que recebe os timestamps como parâmetros, **sem** chamar `create()`.
- **Soft delete**:
  - O domínio marca a exclusão via `entidade.delete()` (seta `deletedAt`).
  - O gateway persiste a mudança chamando `update()`, **não** `delete()`.
  - `@SQLDelete` do Hibernate não deve ser usado — o controle é do domínio.
  - `@SQLRestriction("deleted_at IS NULL")` é usado nas entidades JPA para filtrar automaticamente queries JPQL padrão (findAll, findById).
  - **Native queries não são cobertas por `@SQLRestriction`** — todas as native queries devem incluir `deleted_at IS NULL` explicitamente, incluindo queries de unicidade (ex.: `existsByEmail`).
  - Consultas em tabelas associadas (ex.: items de ordem de serviço) que fazem join com entidades soft-deletáveis também devem considerar o filtro.
- **Infraestrutura**:
  - `AuditEntity` (em `infra/audit`) é a contraparte JPA, mapeia as colunas `date_created`, `date_updated`, `deleted_at`.
  - `@CreationTimestamp` e `@UpdateTimestamp` não devem ser usados — os timestamps vêm do domínio.
  - O mapper JPA sincroniza os timestamps do domínio com a entidade JPA nos dois sentidos.

### 3.10. Testes
- Testar domínio com unit tests puros.
- Testar use cases com mocks da porta.
- Testar controller com integração web e validação.
- Testar repositório/adapter com integração de persistência quando necessário.

## 4. Checklist Prático para Criar uma Nova Feature

Ao iniciar uma feature nova, seguir este checklist:

1. Definir a entidade de domínio e suas regras — estender `AuditDomain` se houver auditoria.
2. Criar as exceções do módulo em uma classe centralizada `[Dominio]Exceptions`.
3. Criar a porta de persistência/integração no core — usar `exists*` booleanos para consultas de unicidade.
4. Criar os commands/queries do core como records, com anotações `jakarta.validation` para validação de formato.
5. Implementar os use cases no core — implementar `UseCase<I, O>`, validar apenas regras de negócio.
6. Criar DTOs de request/response na infra.
7. Criar controller e fazer o mapeamento DTO ↔ command/query.
8. Criar entidade JPA, mapper (sincronizando timestamps) e implementação da porta.
9. Registrar os beans da feature na configuração da infraestrutura.
10. Adicionar tratamento de exceções (`DomainExceptionCore`, `TechnicalException`, `ConstraintViolationException`) e chaves de i18n necessárias.
11. Escrever testes unitários e de integração por camada.
12. Garantir que o core não importe nada de `infra`, `springframework.web` ou `jakarta.persistence` (anotações `jakarta.validation` são permitidas em commands).

## 5. Consequências

### Positivas
- Menor acoplamento entre negócio e tecnologia.
- Mais facilidade para testar o core sem contexto Spring.
- Mudanças de API ou persistência impactam menos o domínio.
- Estrutura mais previsível para novas features.
- Validação declarativa em commands reduz código boilerplate manual nos use cases.
- Auditoria controlada pelo domínio garante consistência independente da tecnologia de persistência.
- Gateways com contratos descritivos (`exists*`) reduzem o acoplamento e deixam a intenção mais clara.
- Melhor alinhamento com as ADRs de lógica no domínio, exceções e i18n.

### Negativas / Custos
- Mais arquivos por feature.
- Mais etapas de mapeamento entre borda e core.
- Maior disciplina necessária para não “vazar” dependências da infra para o core.
- Pode haver sensação de boilerplate em features pequenas, mas isso é aceito em troca de isolamento e clareza arquitetural.
- Validação de formato fica apenas nos DTOs de request da infraestrutura, o que significa que comandos/queries do core são simples records sem validação embutida — o use case recebe dados já validados pelo controller.

## 6. Relação com Outras ADRs

Esta ADR complementa e reforça:

- [ADR 003 - Lógica de Negócio no Domínio](docs/adr/003-logica_negocio_dominio.md)
- [ADR 004 - Padrão de Exceções Modulares](docs/adr/004-padrao_excecoes_modulares.md)
- [ADR 006 - Contrato de Exceções de Domínio e i18n](docs/adr/006-contrato_excecoes_dominio_i18n.md)
- [ADR 010 - Estratégia de i18n e Múltiplos Idiomas](docs/adr/010-estrategia_i18n_multi_idioma.md)

Substitui:

- [ADR 002 - Padrão de UseCases e Commands](docs/adr/002-padrao_usecases_commands.md)
- [ADR 005 - Estratégia de Soft Delete e Auditoria](docs/adr/005-estrategia_soft_delete_auditoria.md)


