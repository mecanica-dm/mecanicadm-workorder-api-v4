# Subindo o sistema Mecânica DM

> Este documento é idêntico nos três repositórios (Workorder API, Finance API e Saga API). Ao mudar um, atualize os três.

O sistema tem **três microsserviços** e um **broker RabbitMQ**. Eles só funcionam **juntos**: a Workorder e o Finance não se
chamam por HTTP, e toda a integração passa pelo broker e pela saga. Por isso **subir um serviço sozinho não basta** para ver o
fluxo de uma OS: ele sobe, responde ao Swagger e aceita cadastros, mas as mensagens ficam sem destino (a Workorder acumula
eventos na outbox; a saga e o Finance não recebem nada).

```
Saga API (8081) ── hospeda o broker RabbitMQ (5672, painel 15672) e o MongoDB
Workorder API (8080) ──┐
Finance API (8082) ────┴── conectam-se a esse mesmo broker
```

## O que você precisa

- **Docker** com **Docker Compose v2.20 ou mais novo** (`docker compose version`). Não precisa de Java nem de Maven: a imagem
  de cada API compila o projeto.
- Cerca de **4 GB de memória livre** e as portas abaixo livres.

| Serviço | Porta no host | Endereço |
|---|---|---|
| Workorder API | 8080 | `http://localhost:8080/api` (Swagger em `/api/swagger-ui.html`) |
| Saga API | 8081 | `http://localhost:8081/api` (Swagger em `/api/swagger-ui.html`) |
| Finance API | 8082 | `http://localhost:8082/api` (Swagger em `/api/swagger-ui.html`) |
| RabbitMQ | 5672 (AMQP) e 15672 (painel) | `http://localhost:15672`, usuário e senha `guest` |
| Mailpit (e-mails de teste) | 8025 (painel) e 1025 (SMTP) | `http://localhost:8025` |
| PostgreSQL da Workorder | 5432 | |
| PostgreSQL do Finance | 5433 | |
| MongoDB da saga | 27017 | |

Para mudar uma porta, defina a variável no `.env` do serviço (`WORKORDER_PORT`, `SAGA_PORT`, `FINANCE_PORT`, `BROKER_PORT`,
`BROKER_MANAGEMENT_PORT`, `WORKORDER_DB_PORT`, `FINANCE_DB_PORT`, `SAGA_MONGO_PORT`, `MAILPIT_UI_PORT`). Se mudar a porta do
Finance, ajuste também `FINANCE_PUBLIC_URL`, porque ela vai nos links do e-mail de orçamento.

## Subir tudo

**Com um comando** (se as três pastas estão lado a lado, na pasta que as contém):

```bash
./subir-tudo.sh        # sobe o broker, a saga, a Workorder e o Finance, na ordem certa, e espera ficarem saudáveis
./derrubar-tudo.sh     # derruba tudo (com --limpar, apaga também os bancos e o broker)
```

**Ou manualmente, nesta ordem** (a primeira subida compila as imagens e leva alguns minutos):

| # | Pasta | Comando | O que sobe |
|---|---|---|---|
| 1 | `mecanicadm-saga-api-v4` | `docker compose --profile broker up -d --build` | MongoDB, **RabbitMQ com a topologia dos três serviços** e a Saga API |
| 2 | `mecanicadm-workorder-api-v4` | `docker compose up -d --build` | PostgreSQL, Workorder API e o seed de dados de exemplo |
| 3 | `mecanicadm-finance-api-v4` | `docker compose up -d --build` | PostgreSQL, Mailpit, Finance API e o seed |

O **broker precisa subir primeiro** (passo 1): ele é criado pelo compose da saga, com o perfil `broker`, e aplica a topologia
de exchanges, filas e DLQs dos três serviços. A Workorder e o Finance o alcançam em `host.docker.internal:5672` (porta
publicada no host), sem rede compartilhada entre os projetos.

Para conferir:

```bash
curl -s localhost:8081/api/actuator/health/liveness   # {"status":"UP"} (saga)
curl -s localhost:8080/api/actuator/health/liveness   # {"status":"UP"} (Workorder)
curl -s localhost:8082/api/actuator/health/liveness   # {"status":"UP"} (Finance)
```

No painel do RabbitMQ (`http://localhost:15672`, aba *Queues*), as filas `workorder`, `budget`, `payment`,
`saga.workorder-events` e `saga.finance-events` devem existir, e as três primeiras mostram 1 consumidor.

### Usando um broker externo (por exemplo, o CloudAMQP)

Não use o perfil `broker`. Em **cada um** dos três serviços, defina no `.env` o mesmo broker: `BROKER_HOST`, `BROKER_PORT`
(5671 com TLS), `BROKER_USER`, `BROKER_PASSWORD`, `BROKER_VHOST` e `BROKER_SSL_ENABLED=true`. Importe antes a topologia
`mecanicadm-saga-api-v4/messaging/rabbitmq-definitions.json` no broker.

## Configuração

O sistema sobe **sem nenhum `.env`**, com valores de desenvolvimento. Para personalizar, copie `.env.example` para `.env` em
cada pasta.

| Variável | Em quais serviços | O que faz |
|---|---|---|
| `JWT_SECRET` | os três | Segredo do JWT. **Precisa ser o mesmo nos três**: a Workorder emite o token no login, e os outros o validam |
| `BROKER_*` | os três | Broker RabbitMQ usado pelo compose (padrão: o broker local da saga) |
| `MERCADO_PAGO_ACCESS_TOKEN`, `MERCADO_PAGO_PUBLIC_KEY` | Finance | Credenciais de **teste** do Mercado Pago, necessárias para o pagamento |
| `MERCADO_PAGO_WEBHOOK_SECRET` | Finance | Assinatura secreta do webhook (veja abaixo) |
| `FINANCE_PUBLIC_URL` | Finance | Endereço público da Finance API, usado nos links do e-mail de orçamento |
| `SMTP_*` | Finance | SMTP dos e-mails de orçamento (padrão: o Mailpit local) |

> **Atenção ao `.env` antigo:** o compose usa variáveis próprias (`BROKER_*`, `SMTP_*`, `FINANCE_PUBLIC_URL`) e **não herda**
> `RABBITMQ_HOST`, `MAIL_*` ou `BUDGET_BASE_URL` de um `.env` feito para rodar pela IDE. Essas continuam valendo só para rodar a
> API fora do Docker.

## Roteiro de demonstração

Dados do seed: usuário `admin@mecanicadm.com` com a senha `Senha123`; clientes, veículos (`ABC-1234`...), mão de obra e
materiais de exemplo. As OS do seed entram direto no banco e **não têm saga**; para ver o fluxo, abra uma OS pela API. Os
comandos usam `curl` e `jq` (ou faça o mesmo pelo Swagger).

```bash
# 1. Login (Workorder): o token vale nos três serviços
TOKEN=$(curl -s localhost:8080/api/user/login -H 'Content-Type: application/json' \
  -d '{"email":"admin@mecanicadm.com","password":"Senha123"}' | jq -r .access_token)
AUTH="Authorization: Bearer $TOKEN"

# 2. Abre uma OS (cliente, veículo, 1 mão de obra e 2 materiais do seed)
OS=$(curl -s localhost:8080/api/work-orders -H "$AUTH" -H 'Content-Type: application/json' -d '{
  "clientId":"150b06cf-68b6-455a-94dc-215c9a0f443b","vehicleId":"ABC-1234","description":"Revisão e troca de óleo",
  "laborIds":["1b913e8a-81cf-41c3-bbbd-6cd8342460e7"],
  "materialItems":[{"materialId":"b3c1b691-8d2b-42ea-a4b5-ea93db622616","quantity":2}]}' | tr -d '"')

# 3. Diagnostica: a saga pede o orçamento ao Finance (estado: BUDGET_GENERATED)
curl -s -X POST localhost:8080/api/work-orders/$OS/step/diagnose -H "$AUTH"
curl -s "localhost:8081/api/sagas?workOrderId=$OS" -H "$AUTH" | jq '.content[0] | {state, step, budgetTotal}'

# 4. O atendente envia o orçamento ao cliente (Finance). O e-mail chega no Mailpit: http://localhost:8025
curl -s -X POST localhost:8082/api/budgets/$OS/send -H "$AUTH"

# 5. O cliente aprova pelo link do e-mail (rota pública, sem JWT; o token do link vem na URL do e-mail)
#    Abra o e-mail no Mailpit e clique em "Aprovar". A saga vai para AWAITING_EXECUTION.

# 6. Execução: inicia a OS, inicia e finaliza a tarefa, finaliza a OS
curl -s -X POST localhost:8080/api/work-orders/$OS/step/start-execution -H "$AUTH"
LABOR=$(curl -s localhost:8080/api/work-orders/$OS -H "$AUTH" | jq -r '.laborItems[0].id')
curl -s -X POST localhost:8080/api/work-orders/$OS/labors/$LABOR/start  -H "$AUTH"
curl -s -X POST localhost:8080/api/work-orders/$OS/labors/$LABOR/finish -H "$AUTH"
curl -s -X POST localhost:8080/api/work-orders/$OS/step/finish-execution -H "$AUTH"

# 7. Histórico de status da OS (Workorder) e histórico completo da saga (Saga)
curl -s localhost:8080/api/work-orders/$OS/history -H "$AUTH" | jq
curl -s localhost:8081/api/sagas/$OS -H "$AUTH" | jq '.saga.state, (.history[] | [.eventType, .toState])'
```

Ao finalizar a execução, a saga pede o pagamento ao Finance. **Sem as credenciais do Mercado Pago o pagamento falha**, e a saga
fica em `PAUSED` com o motivo `PaymentLinkGenerationFailed`. Isso é o esperado em um ambiente sem credenciais: com
`MERCADO_PAGO_ACCESS_TOKEN` e `MERCADO_PAGO_PUBLIC_KEY` de teste no `.env` do Finance (e o cartão de teste padrão, titular
`APRO`), o pagamento é aprovado, a saga libera o veículo (`ReleaseWorkOrderVehicle`) e conclui quando a OS é entregue
(`POST /api/work-orders/{id}/step/deliver`).

**Para ver a compensação:** cancele a OS antes do pagamento (`POST /api/work-orders/{id}/step/cancel`). A saga cancela o
orçamento no Finance, espera a confirmação e vai para `CANCELED`. Para listar o que está parado:
`curl -s "localhost:8081/api/sagas?state=PAUSED" -H "$AUTH"`.

### Webhook do Mercado Pago

O resultado de um pagamento pendente chega ao Finance pelo webhook (e, como reconciliação, por uma rotina de consulta). Para
usá-lo: (1) exponha a Finance API em uma URL pública HTTPS (`ngrok http 8082`); (2) no painel do Mercado Pago, em *Suas
integrações, Webhooks*, informe `https://<url-publica>/api/v1/payments/webhooks/mercado-pago` e marque o evento *Order
(Mercado Pago)*; (3) copie a **assinatura secreta** para `MERCADO_PAGO_WEBHOOK_SECRET` no `.env` do Finance e reinicie a API
(`docker compose up -d`). Sem o segredo, o webhook responde 503 e só a rotina de consulta atua.

## Parar e limpar

```bash
./derrubar-tudo.sh              # para os contêineres (os dados ficam nos volumes)
./derrubar-tudo.sh --limpar     # para e apaga os bancos e o broker (recomeça do zero)
```

Ou, em cada pasta: `docker compose down` (e `-v` para apagar o volume). Na saga, use `docker compose --profile broker down`.

## Problemas comuns

| Sintoma | Causa provável e solução |
|---|---|
| A OS é criada, mas a saga não a vê | O broker não está no ar, ou a topologia não foi aplicada. Suba a saga com `--profile broker` **antes** dos outros e confira as filas no painel (`localhost:15672`) |
| `401` ao chamar a saga ou o Finance com o token da Workorder | O `JWT_SECRET` é diferente entre os serviços. Deixe o mesmo valor nos três e reinicie |
| Workorder ou Finance não alcançam o broker | No Linux, `host.docker.internal` depende do `extra_hosts` do compose (Docker 20.10+). Confirme que o broker publicou a porta 5672 no host |
| A saga está `PAUSED` | `curl -s "localhost:8081/api/sagas?state=PAUSED" -H "$AUTH"` mostra o motivo (`pauseReason`). Sem credenciais do Mercado Pago, é `PaymentLinkGenerationFailed` |
| O e-mail do orçamento não chega | Os e-mails de teste ficam no Mailpit (`localhost:8025`), não numa caixa real. Para enviar de verdade, defina `SMTP_*` |
| O link do e-mail aponta para a porta errada | Ajuste `FINANCE_PUBLIC_URL` no `.env` do Finance |
| `port is already allocated` | Outra aplicação usa a porta. Mude a variável `*_PORT` correspondente |
| Mensagens paradas em `*.dlq` | Falha técnica no consumo. Veja os logs (`docker compose logs api`) e reprocesse depois de corrigir a causa |
