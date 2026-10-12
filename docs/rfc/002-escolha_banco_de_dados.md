# RFC 002 - Escolha do Banco de Dados

* **Status**: Aprovado
* **Data**: 11/08/2026
* **Autores**: Time de desenvolvimento
* **Resumo**: Este documento propõe a adoção do **PostgreSQL** hospedado em **RDS** (AWS) como banco de dados da API da
  Mecânica DM, avaliando alternativas como MySQL e MongoDB.

---

## 1. Resumo

A equipe precisa definir o banco de dados da API de gestão de oficinas. Recomendamos **PostgreSQL** hospedado no
**Amazon RDS**, aproveitando a infraestrutura AWS já definida na [RFC 001](001-escolha_de_nuvem.md).

## 2. Contexto

O projeto já utiliza **PostgreSQL com Flyway** para migrations e **Testcontainers com PostgreSQL** para os testes de
integração, conforme documentado no `README`. A Fase 03 requer um banco gerenciado, resiliente e com backups automáticos.

## 3. Objetivos

- Manter compatibilidade com o stack já existente (JPA, Flyway, Testcontainers em testes).
- Garantir integridade relacional e transações ACID.

## 4. Alternativas Consideradas

### 4.1. PostgreSQL no Amazon RDS

**Prós:**

- Já adotado no projeto (compatibilidade total com Flyway e Spring Data JPA);
- **RDS**: backups automáticos, Multi-AZ, failover gerenciado;
- Suporte a tipos avançados (JSONB, arrays) e constraints ricas;
- Comunidade madura e integração nativa com Terraform.

**Contras:**

- Custo de instâncias RDS pode ser maior que banco em container.

### 4.2. PostgreSQL em container (EKS) em vez de RDS

**Prós:**

- Sem custo adicional de serviço gerenciado.

**Contras:**

- Persistência do banco em `PersistentVolume` no EKS aumenta complexidade e risco;
- Conflita com o requisito de resiliência da Fase 03.

## 5. Proposta

Adotar **PostgreSQL no Amazon RDS** como banco de dados, com a seguinte configuração de referência:

| Componente     | Detalhe                                           | Finalidade                    |
|----------------|---------------------------------------------------|-------------------------------|
| Engine         | PostgreSQL (compatível com Flyway)                | Persistência relacional       |
| Serviço        | Amazon RDS (Single-AZ, possibilidade de Multi-AZ) | Banco gerenciado e resiliente |
| Acesso         | Subnet privada, security group restrito           | Isolamento e segurança        |
| Migrations     | Flyway                                            | Versionamento do schema       |
| Backups        | Automáticos + retention configurada no Terraform  | Recuperação de dados          |
| Infraestrutura | Terraform (módulo `infra/terraform`)              | Provisionamento declarativo   |

A string de conexão será configurada via variável de ambiente no Kubernetes, sem expor credenciais no repositório.

## 6. Impacto e Riscos

- **Risco**: custo imprevisto da instância RDS.
    - **Mitigação**: escolher classe de instância adequada.
- **Risco**: vazamento das credenciais armazenadas em segredo.
    - **Mitigação**: utilizar Kubernetes Secrets / AWS Secrets Manager, sem hardcode.

## 7. Implementação

1. Criar o módulo Terraform do RDS PostgreSQL no repositório db.
2. Configurar subnet privada, security group e credenciais de acesso.
3. Configurar a aplicação (Spring Boot) para usar a string de conexão via variável de ambiente.
4. Garantir que o Flyway rode as migrations no ambiente RDS.

## 8. Decisão Final

| Item           | Detalhe                 |
|----------------|-------------------------|
| Banco de dados | PostgreSQL (Amazon RDS) |
| Status         | Proposta — aprovada     |
