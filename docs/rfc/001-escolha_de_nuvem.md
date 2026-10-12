# RFC 001 - Escolha do Provedor de Nuvem

* **Status**: Aprovado
* **Data**: 09/08/2026
* **Autores**: Time de desenvolvimento
* **Resumo**: Este documento propõe a adoção do **AWS** como provedor de nuvem para hospedar a API da Mecânica DM.

---

## 1. Resumo

A equipe precisa definir um provedor de nuvem para hospedar a API de gestão de oficinas. Recomendamos **AWS** como
provedor principal, utilizando **EKS** para orquestração de containers e **Terraform** para provisionamento de
infraestrutura.

## 2. Contexto

A aplicação Mecânica DM é uma API Java (Spring Boot 3) com PostgreSQL, containerizada via Docker e com suporte a
múltiplos idiomas. O projeto está entrando na **Fase 03**.

Precisamos de um provedor que ofereça gerenciamento de Kubernetes gerenciado (managed), suporte a PostgreSQL e
facilidade de integração com pipelines de CI/CD.

## 3. Objetivos

- Manter custos previsíveis e poder acompanhar evolução dos custos.
- Permitir escalabilidade horizontal (HPA).

## 4. Alternativas Consideradas

Para este projeto, consideramos apenas a AWS pois é a plataforma recomendada pelos professores.

### 4.1. AWS (Amazon Web Services)

**Prós:**

- **EKS**: Kubernetes gerenciado próprio da AWS;
- **VPC**: controle de rede (subnets públicas/privadas, NAT gateway);
- **RDS**: PostgreSQL gerenciado;
- Integração nativa com Terraform.

**Contras:**

- Curva de aprendizado;
- Documentação extensa e as vezes dispersa;
- Custo pode escalar com recursos não monitorados.

## 5. Proposta

Adotar **AWS** como provedor de nuvem, com a seguinte arquitetura de referência:

| Componente     | Serviço AWS                       | Finalidade                  |
|----------------|-----------------------------------|-----------------------------|
| Rede           | VPC com subnets públicas/privadas | Comunicação                 |
| Orquestração   | EKS (Kubernetes gerenciado)       | Execução dos containers     |
| Banco de dados | RDS PostgreSQL                    | Persistência                |
| Balanceamento  | Load Balancer                     | Distribuição de tráfego     |
| Infraestrutura | Terraform                         | Provisionamento declarativo |

A infraestrutura será declarada em Terraform para permitir revisão e versionamento.

## 6. Impacto e Riscos

- **Risco**: custo imprevisto de recursos EKS não utilizados.
    - **Mitigação**: utilização de HPA, `resource requests/limits` e revisão periódica de custos.
- **Risco**: complexidade de rede (VPC, NAT, security groups).
    - **Mitigação**: documentar a topologia em `README`.

## 7. Implementação

1. Escrever os manifestos Kubernetes no repositório k8s.
2. Provisionar a infraestrutura com o Terraform no repositório k8s.
3. Configurar o pipeline de CI/CD para deploy na AWS.

## 8. Decisão Final

| Item     | Detalhe             |
|----------|---------------------|
| Provedor | AWS                 |
| Status   | Proposta — aprovada |
