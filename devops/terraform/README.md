# Terraform — Banco de dados da Work Order API

Infraestrutura AWS do serviço `mecanicadm-workorder-api-v4`. Este diretório é
aplicado **depois** do [`mecanicadm-infra-v4`](../../../mecanicadm-infra-v4),
que cria a VPC, o cluster EKS e publica os parâmetros SSM usados aqui.

## O que é criado

| Recurso | Finalidade |
|---|---|
| `aws_db_instance` PostgreSQL | Banco do serviço (`workorder_db`) em subnet privada |
| `aws_db_subnet_group` | Subnets privadas descobertas pela tag `Name` do módulo VPC |
| `aws_security_group` do RDS | Libera 5432 apenas para o SG dos worker nodes do EKS |
| IAM Role de Enhanced Monitoring | Métricas granulares do RDS no CloudWatch |
| `aws_secretsmanager_secret` de credenciais | Host, porta, banco, usuário e senha do RDS |
| `aws_secretsmanager_secret` de JWT | Segredo HMAC compartilhado entre os serviços v4 |
| Parâmetros SSM | `db_host`, `db_port`, `db_name`, `db_user`, `db_password`, `db_sslmode` e `jwt_secret` |

O RDS é **privado** (`publicly_accessible = false`) e só aceita conexões do
security group dos nodes do EKS (`/mecanicadm/prod/eks_node_sg_id`). Não há
nenhum CIDR liberado por padrão.

## O que **não** é criado aqui

- **RabbitMQ**: o broker é gerenciado no CloudAMQP (plano compartilhado Little
  Lemur) e **não** é criado por este state. As credenciais ficam no SSM em
  `/mecanicadm/prod/rabbitmq/{user,password,vhost}` — publicadas pelas pipelines
  de deploy (finance-api e workorder) a partir dos GitHub Secrets
  `CLOUDAMQP_*` — e são materializadas no Secret `mecanicadm-secrets`. A
  pipeline do workorder cria somente os parâmetros ausentes; o finance
  sobrescreve os existentes a cada deploy dele. O host e a porta (`5671`, TLS)
  ficam no ConfigMap. A topologia (`messaging/workorder-definitions.json`)
  precisa ser importada no vhost do CloudAMQP antes do deploy: troque os
  `"vhost": "/"` pelo vhost real da instância e importe via
  `POST https://<host>/api/definitions` ou pela Management UI
  (`https://<host>/`). A aplicação **não** declara a topologia em runtime — sem
  a fila `workorder`, o listener fica em retry.
- **Namespace, Ingress, Kong, Route 53, HPA, image pull**: pertencem ao
  `mecanicadm-infra-v4` e aos manifests em `devops/k8s`.
- **Segredos de e-mail e New Relic**: continuam vindo dos GitHub Secrets do
  workflow de deploy.

## Pré-requisitos

1. `mecanicadm-infra-v4` aplicado em `prod`.
2. Parâmetros SSM existentes:
   - `/mecanicadm/prod/vpc_id`
   - `/mecanicadm/prod/eks_node_sg_id`
3. Bucket do state (`mecanicadm-v4-01`) e credenciais AWS com permissão para
   RDS, EC2 (SG), IAM, Secrets Manager e SSM.
4. Terraform >= 1.10.

## Backend

O state fica separado do cluster:

```hcl
# backend.hcl
bucket       = "mecanicadm-v4-01"
region       = "us-east-1"
key          = "terraform/mecanicadm-workorder/terraform.tfstate"
encrypt      = true
use_lockfile = true
```

```bash
terraform init -backend-config=backend.hcl
```

Há um modelo em [`backend.hcl.example`](backend.hcl.example).

## Aplicação

```bash
terraform init -backend-config=backend.hcl
terraform fmt -check -recursive -diff
terraform validate
terraform plan -out=tfplan
terraform apply tfplan
```

Para sobrescrever configurações:

```bash
cp terraform.tfvars.example terraform.tfvars
```

## Aplicação via GitHub Actions (sem apply local)

O workflow [`.github/workflows/terraform.yml`](../../.github/workflows/terraform.yml)
executa `init` → `fmt` → `validate` → `plan` → `apply` no GitHub Actions:

- **Pull request**: apenas `plan` (validação).
- **Push na `main`** que altere `devops/terraform/**`: `apply` automático,
  protegido pelo environment `prod` (configure aprovação manual nele, se
  quiser).
- **workflow_dispatch**: execução manual, útil para o primeiro apply.

Pré-requisitos no repositório:

- Secrets `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` (os mesmos usados pelo
  deploy).
- Opcional: variáveis `AWS_REGION` e `TF_STATE_BUCKET` (defaults:
  `us-east-1` e `mecanicadm-v4-01`).
- Permissões IAM para RDS, EC2 (SG), IAM, Secrets Manager, SSM e no bucket do
  state.

Depois que o workflow de Terraform termina com sucesso, o
[`app-deploy.yml`](../../.github/workflows/app-deploy.yml) é disparado via
`workflow_run` e cria os Secrets a partir do SSM antes de aplicar os manifests.
Na primeira vez, rode o workflow **Terraform - Workorder DB + Secrets** na
`main`; não é necessário `terraform apply` na sua máquina.

## Como a pipeline consome

O workflow `.github/workflows/app-deploy.yml` cria os Secrets do Kubernetes
diretamente a partir do SSM, na seguinte ordem:

- `mecanicadm-db-secrets`: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` e
  `DB_PASSWORD`, lidos de `/mecanicadm/prod/workorder/*`.
- `mecanicadm-secrets`: credenciais de e-mail, New Relic (GitHub Secrets),
  `JWT_SECRET`, lido de `/mecanicadm/prod/jwt_secret`, e as credenciais do
  CloudAMQP (`RABBITMQ_USER`, `RABBITMQ_PASSWORD` e `RABBITMQ_VHOST`), lidas de
  `/mecanicadm/prod/rabbitmq/*`. Antes de montar o Secret, a pipeline publica
  nesse prefixo os parâmetros que ainda não existirem, usando os GitHub Secrets
  `CLOUDAMQP_USER`, `CLOUDAMQP_PASSWORD` e `CLOUDAMQP_VHOST` deste repositório
  — ou seja, não depende de o finance-api ter feito deploy antes. Se os
  parâmetros já existirem, os valores atuais são mantidos (o finance os
  sobrescreve no deploy dele).

Os comandos equivalentes para depuração manual são:

```bash
DB_HOST=$(aws ssm get-parameter --name /mecanicadm/prod/workorder/db_host --with-decryption --query Parameter.Value --output text)
DB_PORT=$(aws ssm get-parameter --name /mecanicadm/prod/workorder/db_port --query Parameter.Value --output text)
DB_NAME=$(aws ssm get-parameter --name /mecanicadm/prod/workorder/db_name --query Parameter.Value --output text)
DB_USER=$(aws ssm get-parameter --name /mecanicadm/prod/workorder/db_user --with-decryption --query Parameter.Value --output text)
DB_PASSWORD=$(aws ssm get-parameter --name /mecanicadm/prod/workorder/db_password --with-decryption --query Parameter.Value --output text)
JWT_SECRET=$(aws ssm get-parameter --name /mecanicadm/prod/jwt_secret --with-decryption --query Parameter.Value --output text)
RABBITMQ_USER=$(aws ssm get-parameter --name /mecanicadm/prod/rabbitmq/user --with-decryption --query Parameter.Value --output text)
RABBITMQ_PASSWORD=$(aws ssm get-parameter --name /mecanicadm/prod/rabbitmq/password --with-decryption --query Parameter.Value --output text)
RABBITMQ_VHOST=$(aws ssm get-parameter --name /mecanicadm/prod/rabbitmq/vhost --with-decryption --query Parameter.Value --output text)
```

Os valores precisam bater com o que a aplicação espera (`DB_HOST`, `DB_PORT`,
`DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `RABBITMQ_USER`,
`RABBITMQ_PASSWORD` e `RABBITMQ_VHOST`).

> `devops/k8s/secret.example.yaml` é apenas referência: não é aplicado pela
> pipeline e não deve ser usado no deploy real. O Secret real é criado pelo
> workflow a partir do SSM.

## Descoberta das subnets

O `mecanicadm-infra-v4` ainda não publica os IDs das subnets no SSM. Por isso o
Terraform localiza as subnets privadas pela tag `Name` gerada pelo módulo VPC:

```
mecanicadm-prod-private-us-east-1a
mecanicadm-prod-private-us-east-1b
```

Se a topologia mudar, informe os IDs explicitamente:

```hcl
private_subnet_ids = ["subnet-xxxxxxxx", "subnet-yyyyyyyy"]
```

A validação exige pelo menos 2 subnets em AZs diferentes.

## Custos e destruição

- A classe padrão é `db.t4g.micro`, Single-AZ, 20 GB gp3, backup de 1 dia e
  Performance Insights com 7 dias (free tier). Ajuste em `variables.tf` ou
  `terraform.tfvars`.
- `deletion_protection = false` e `skip_final_snapshot = true` por padrão para
  permitir `terraform destroy` no ambiente de estudo. Em produção real, use
  `deletion_protection = true` e `skip_final_snapshot = false`.
- `secret_recovery_window_in_days = 0` permite recriar os secrets logo após um
  destroy. Em produção real, use 7–30 dias.

## Próximos passos sugeridos

1. Importar a topologia de `messaging/workorder-definitions.json` no vhost do
   CloudAMQP (trocando os `"vhost": "/"` pelo vhost real da instância). Sem ela
   o listener não consome a fila `workorder`.
2. Se as credenciais do broker mudarem, atualize os GitHub Secrets
   `CLOUDAMQP_USER`, `CLOUDAMQP_PASSWORD` e `CLOUDAMQP_VHOST` no repositório do
   finance-api e rode o deploy dele (ele sobrescreve os parâmetros no SSM).
   Esta pipeline lê os valores publicados e reinicia o Deployment.
