# [007]: [Pirâmide e Tipos de Testes]

* **Status**: Aceito
* **Data**: 24/05/2024

## 1. Contexto e Problema

Para garantir a qualidade do software de forma eficiente, é necessário definir quais tipos de testes devem ser priorizados e como eles devem ser estruturados no projeto.

## 2. Decisão

Adotaremos a estratégia da Pirâmide de Testes, focando em testes de unidade e integração.

- **Testes de Unidade**: Devem focar na lógica de negócio das entidades de domínio e nos serviços (quando não dependem de IO). Devem ser rápidos e não carregar o contexto do Spring. Utilizar JUnit 5 e Mockito.
- **Testes de Integração (IT)**: Devem validar a integração entre componentes, camadas da aplicação (API -> UseCase -> DB) e contratos externos. Utilizam o contexto do Spring (`@SpringBootTest`).
- **Priorização**: A maior parte da cobertura deve vir de testes de unidade para garantir feedback rápido. Testes de integração devem cobrir os fluxos principais (caminho feliz) e casos de erro críticos de integração.

### Exemplo de Estrutura de Pacotes

- Testes de unidade: `src/test/java/.../domain` e `src/test/java/.../service`
- Testes de integração: `src/test/java/.../adapter/api` (sufixo `IT.java`)

