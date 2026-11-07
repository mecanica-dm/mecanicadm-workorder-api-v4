# Mensageria da workorder-api: como funciona

Este documento explica, do zero, como a workorder-api troca mensagens com os outros serviços pelo RabbitMQ. Ele não assume que você conhece Outbox, Inbox, ack ou exchanges. Os conceitos vêm primeiro, depois o fluxo e o mapa do código.

---

## 1. O problema

Os serviços (workorder-api, finance-api, saga-api) não se chamam por HTTP. Eles trocam **mensagens** pelo **RabbitMQ**, que funciona como um correio no meio.

Existem dois tipos de mensagem:

| Tipo | Significado | Exemplo |
|---|---|---|
| **Evento** | "Isto **já aconteceu**" (passado) | `WorkOrderCanceledEvent` |
| **Command** | "Faça isto" (pedido) | `CancelWorkOrderIntegrationCommand` |

Trocar mensagens traz dois problemas clássicos.

**Problema A: salvar no banco e publicar não é uma operação só.**

```
1. salva a OS no banco     ✔
2. publica o evento        ✘  (RabbitMQ fora do ar, ou a aplicação caiu agora)
```

A OS mudou, mas ninguém soube. Se você inverter a ordem, o evento pode sair e o banco falhar, e os outros serviços acreditam em algo que não aconteceu.

**Problema B: o correio pode entregar a mesma carta duas vezes.**

O RabbitMQ prefere entregar **duas vezes** a **zero vezes** (entrega *at-least-once*). Se a aplicação executou uma ordem mas caiu antes de avisar o RabbitMQ, ele entrega de novo, e a ordem seria executada duas vezes.

A solução é o par **Outbox** (resolve A) e **Inbox** (resolve B).

---

## 2. Os conceitos

### 2.1 Outbox (caixa de saída)

> Em vez de publicar o evento direto no RabbitMQ, **grave o evento numa tabela do próprio banco, na mesma transação que altera a OS**. Depois, um processo separado lê a tabela e publica.

Pense numa empresa com uma caixa de saída na mesa. Ao terminar um serviço, você coloca a carta na caixa **junto com** o registro do serviço, num único ato. Depois, um office-boy recolhe as cartas e leva ao correio.

Como o banco garante que **ou tudo é gravado, ou nada é**, nunca existe "OS alterada sem evento" nem "evento de uma OS que deu rollback". Se o RabbitMQ estiver fora do ar, as cartas esperam na caixa e saem quando ele voltar.

### 2.2 Inbox (caixa de entrada)

> Quando chega um command, **anote numa tabela que ele já foi processado** (pelo `messageId`). Se a mesma mensagem chegar de novo, você vê que já foi feita e **ignora**.

É um caderno de protocolo: cada carta tem um número, e antes de executar a ordem você confere o caderno. O ponto-chave é que **o registro no caderno e o efeito da ordem acontecem na mesma transação**. Assim, ou os dois acontecem, ou nenhum. Se algo falha e tudo é desfeito, o número não fica no caderno e a próxima entrega é processada normalmente.

> **Resumindo:**
> - **Outbox** garante que o que **eu envio** nunca se perde.
> - **Inbox** garante que o que **eu recebo** nunca é executado duas vezes.
>
> São problemas diferentes. Um não substitui o outro.

### 2.3 Vocabulário do RabbitMQ

- **Exchange:** a "agência dos correios". Quem publica entrega a mensagem a uma exchange.
- **Fila (queue):** a "caixa postal" onde as mensagens esperam.
- **Binding:** a regra que liga uma exchange a uma fila ("mensagens com tal característica vão para esta fila").
- **Exchange do tipo headers:** decide o destino olhando os **headers** da mensagem (campos de metadados), e não uma routing key. É o que usamos.
- **Consumer / listener:** quem fica lendo uma fila.
- **Ack:** o consumer diz "terminei, pode apagar". **Sem ack, o RabbitMQ guarda a mensagem** e a reentrega se o consumer cair.
- **Nack:** "não consegui processar".
- **DLQ (dead-letter queue):** fila de "mensagens mortas", para onde vai o que não foi possível processar quando a primeira tentativa falha. Fica guardado para investigação, em vez de travar a fila principal.
- **Publisher confirm:** o RabbitMQ responde ao publicador "recebi e está seguro". Sem isso, um publicador pode achar que enviou sem ter enviado.
- **Idempotência:** operação que, se repetida, tem o mesmo resultado de uma execução só. É o que o inbox garante.

---

## 3. O contrato: headers + corpo

Toda mensagem, evento ou command, segue o mesmo padrão. Os **metadados de transporte vão nos headers AMQP**, e o **corpo** leva só o que é do negócio.

**Headers da mensagem** (padrão do produto):

| Header | Para que serve |
|---|---|
| `message-id` (header, UUID) | Identifica **esta** mensagem. Gerado na publicação. É a chave do inbox e o `id` da linha na outbox. Vai só como header, não como propriedade `message_id` do AMQP. |
| `event-type` (String) | O tipo da mensagem: **o nome da classe** (`CancelWorkOrderIntegrationCommand`, `WorkOrderCanceledEvent`...). É por ele que as filas fazem o bind. |
| `timestamp` (propriedade AMQP) | Quando a mensagem foi publicada (UTC). Também é o `occurred_at` da outbox. |

**Corpo** (cada evento e command é um `record`, sem interface em comum):

| Campo | Para que serve |
|---|---|
| `workOrderId` (UUID) | A OS a que a mensagem se refere. Como a saga é por OS, ele também identifica a saga. |

Cada mensagem acrescenta os **seus próprios campos** no mesmo nível. `message-id`, `event-type` e a data de ocorrência **não** vão no corpo. Exemplo de um command no fio:

```
headers: message-id = b0a1c2d3-...   event-type = CancelWorkOrderIntegrationCommand
```

```json
{
  "workOrderId": "91d4e5f6-..."
}
```

Não existe `correlationId` nem `version`: o `workOrderId` cumpre o papel de identificar a saga.

---

## 4. As exchanges e as filas

Existem 4 exchanges, todas do tipo **headers** e duráveis:

| Exchange | Quem publica | Quem lê |
|---|---|---|
| `workorder-input` | saga e outros, mandando commands | filas de commands da workorder-api |
| `workorder-output` | workorder-api, publicando eventos | filas de quem quer ouvir a workorder-api |
| `finance-input` | quem manda commands ao finance | finance-api |
| `finance-output` | finance-api, publicando eventos | quem quer ouvir o finance |

A regra de roteamento é: **o header AMQP `event-type` da mensagem tem que bater com o `event-type` do bind da fila.** Quem quer receber um tipo de mensagem cria uma fila e faz o bind direto nela, com `x-match = all` e `event-type = <nome da classe>`.

### Quem cria a topologia

A aplicação **não declara** exchanges, filas nem binds. A topologia fica no arquivo `messaging/workorder-definitions.json`, que é aplicado no RabbitMQ por um passo separado antes de a aplicação subir:

- **Local:** o serviço `rabbitmq-init` do `docker-compose.yml` faz `POST /api/definitions` no RabbitMQ (aditivo e idempotente) e termina. A `api` só inicia depois que ele conclui com sucesso.
- **Testes:** o `WorkOrderConsumerIT` aplica o mesmo arquivo no container do RabbitMQ antes de subir o contexto.
- **Kubernetes:** o mesmo arquivo será aplicado por um passo do manifesto do projeto (a definir).

Se a fila não existir, o listener não sobe. O arquivo cria as exchanges `workorder-input` e `workorder-output` (tipo headers), a `saga-output` (a mesma declaração do saga, necessária para o bind existir; ela é idempotente), um bind **`saga-output` → `workorder-input`** que repassa tudo o que o saga publicar, a dead-letter exchange `mecanicadm.dlx`, a fila `workorder`, a `workorder.dlq` e os binds descritos abaixo. Quem consome os eventos da workorder cria a própria fila e o bind na `workorder-output`.

### Fila da workorder-api

A workorder-api tem **uma única fila**, `workorder`, ligada à `workorder-input` com **um bind por command** (todos com `x-match = all`):

| Bind (`event-type`) | Fila |
|---|---|
| `CancelWorkOrderIntegrationCommand` | `workorder` |
| `AuthorizeWorkOrderIntegrationCommand` | `workorder` |
| `ChangesRequestedWorkOrderIntegrationCommand` | `workorder` |
| `ReleaseWorkOrderVehicleIntegrationCommand` | `workorder` |

A fila tem uma DLQ, `workorder.dlq`, ligada à exchange de mortas `mecanicadm.dlx`. Como o serviço ainda é pequeno, uma fila só basta; se crescer, dá para dividir depois.

---

## 5. Fluxo de SAÍDA: eventos (workorder-api → outros)

Exemplo: o usuário diagnostica uma OS pela API.

```
 Requisição HTTP "diagnosticar OS"
        │
        ▼
 ┌───────────────────────────────────────────────┐
 │  UMA TRANSAÇÃO NO BANCO                         │
 │                                                 │
 │  1. DiagnoseWorkOrderUseCase muda a OS           │
 │  2. Cria o WorkOrderDiagnosedEvent               │
 │     (evento já montado)                           │
 │  3. MessagePublisher.publish(workOrderId, evento)│
 │     → na prática, INSERT na tabela "outbox"      │
 │  4. COMMIT (OS e linha da outbox, juntas)        │
 └───────────────────────────────────────────────┘
        │
        │   (depois, de forma independente)
        ▼
 ┌───────────────────────────────────────────────┐
 │  OutboxPublisher  (@Scheduled, a cada 1 s)       │
 │  1. Lê um lote de linhas pendentes               │
 │  2. Envia ao RabbitMQ (exchange workorder-output)│
 │     com o header event-type                       │
 │  3. ESPERA O CONFIRM do RabbitMQ                 │
 │  4. Só então marca a linha como publicada        │
 └───────────────────────────────────────────────┘
        │
        ▼
   exchange workorder-output ──(header event-type)──► filas interessadas
```

### Quem faz o quê

- **`MessagePublisher` (core):** a porta de publicação. Tem um método só, `publish(workOrderId, mensagem)`, e aceita qualquer objeto como mensagem. O use case não sabe se ela vai para o RabbitMQ ou para uma tabela. É a regra da arquitetura limpa: o core não conhece tecnologia.
- **Eventos (core):** cada evento é um `record`. O método `X.of(workOrder, ...)` monta o corpo a partir dos objetos que o use case já tem em mãos. Só Created e Updated carregam os dados da OS (nomes e preços); os demais eventos levam apenas o `workOrderId`, porque quem consome já tem os dados a partir deles. Esses dois recebem um `WorkOrderDetails`, que o use case lê com **uma única consulta**, `WorkOrderGateway.findDetailsById`. É uma query nativa que já devolve a OS com os itens de mão de obra e de material, com nome e preço do cadastro. O evento não acessa gateways. O `messageId` e o `event-type` (nome da classe) são definidos pelo `OutboxMessagePublisher` na publicação.
- **`OutboxMessagePublisher` (infra):** implementa a porta. Faz o `INSERT` na tabela `outbox`. Exige transação aberta (`MANDATORY`), porque a graça é gravar junto com a OS.
- **`TransactionalProxy` (infra):** os use cases não têm `@Transactional`, pois o core não usa Spring. Este proxy abre a transação em volta deles, e é montado na `WorkOrderConfiguration`.
- **`OutboxPublisher` (infra):** um único componente com dois métodos agendados.
  - **`publishPending`** (a cada 1 s): numa transação, trava até 50 linhas pendentes na ordem da `sequence` (`SELECT ... FOR UPDATE`). Para cada uma, envia ao RabbitMQ pelo `RabbitOutboxMessageSender` e, com o confirm, marca `status = PUBLISHED` e preenche `published_at`. **Não há nova tentativa**: se o envio falhar, a linha vira `status = FAILED` com o motivo em `last_error`, e o lote segue com as demais.
  - **`cleanPublished`** (todo dia às 3h): apaga as linhas já publicadas há mais de 7 dias.
- **`RabbitOutboxMessageSender` (infra):** publica na `workorder-output` com o header `event-type` e espera o confirm do broker (até 5 s). Nack ou timeout lançam `IllegalStateException`.
- **Desligar o agendamento:** `messaging.outbox.scheduler.enabled: false` (usado nos testes).

> **Regra importante:** o status só vira `PUBLISHED` **depois** do confirm. Se a aplicação cair entre o confirm e o commit, a mensagem é enviada de novo. Isso é aceitável porque o **receptor deduplica pelo `messageId`**.

### A tabela `outbox`

| Coluna | Para que serve |
|---|---|
| `id` | É o `messageId`. |
| `sequence` | Número crescente global; define a ordem de publicação. |
| `aggregate_id` | A OS (`workOrderId`). |
| `type` | O `event-type`; vai como header AMQP. |
| `payload` | A mensagem completa em JSON. |
| `occurred_at` | Quando ocorreu. |
| `status` | `PENDING` (aguardando), `PUBLISHED` (confirmada pelo broker) ou `FAILED` (falhou; não é reenviada). |
| `published_at` | Quando foi confirmada. |
| `last_error` | Motivo da falha, para diagnóstico. |

### Ordem e várias instâncias

As mensagens saem na ordem da `sequence`. Como o lote é travado com `FOR UPDATE`, uma segunda instância da API **espera** a primeira terminar o lote e depois segue, sem nunca pegar as mesmas linhas. Uma mensagem `FAILED` não bloqueia as seguintes.

### Evento sem ninguém interessado

Se nenhuma fila fizer bind em um `event-type`, o RabbitMQ simplesmente descarta a mensagem, e a aplicação a considera publicada. Isso evita que um evento que ninguém assina trave a outbox, mas significa que **quem ainda não tinha fila no momento da publicação perde o evento**.

---

## 6. Fluxo de ENTRADA: commands (outros → workorder-api)

Exemplo: o saga manda "cancele a OS X".

```
 Saga publica na exchange saga-output
   header event-type = CancelWorkOrderIntegrationCommand
        │  (bind saga-output → workorder-input)
        ▼
 Exchange workorder-input
        │  (bind por event-type)
        ▼
 Fila workorder
        │
        ▼
 ┌───────────────────────────────────────────────┐
 │ WorkOrderConsumer  (@RabbitListener na classe)    │
 │  o conversor lê o header event-type e transforma    │
 │  o corpo no command; o Spring escolhe o método     │
 │  @RabbitHandler pelo tipo (onCancel, onAuthorize…) │
 │  consume(): valida e processa (sem retry)           │
 │  e no fim dá ACK ou NACK (DLQ)                     │
 └───────────────────────────────────────────────┘
        │
        ▼
 ┌───────────────────────────────────────────────┐
 │ process() — UMA TRANSAÇÃO                         │
 │  1. Registra o messageId no INBOX                  │
 │       já existe?  → DUPLICATA: pare                │
 │  2. handle(command) → CancelWorkOrderUseCase       │
 │  3. O use case altera a OS e grava o evento        │
 │     na OUTBOX                                      │
 │  4. COMMIT (inbox + OS + outbox, juntos)           │
 └───────────────────────────────────────────────┘
        │
        ▼
   ACK  (só agora!)
```

### Quem faz o quê

- **Integration commands (infra):** `CancelWorkOrderIntegrationCommand`, `AuthorizeWorkOrderIntegrationCommand`, `ChangesRequestedWorkOrderIntegrationCommand` e `ReleaseWorkOrderVehicleIntegrationCommand` são `record`s em `infra/features/workorder/amqp/command`. Eles são o contrato do que chega do broker (só `workOrderId`, mais `observation` no ChangesRequested). Cada um tem `toCommand()`, que o converte no command do use case (`CancelWorkOrderCommand`, `AuthorizeWorkOrderCommand`, `RequestWorkOrderChangesCommand`, `ReleaseWorkOrderVehicleCommand`). O core não conhece o formato da mensagem.
- **Conversor (`RabbitMQConfig.commandMessageConverter`):** um `Jackson2JsonMessageConverter` configurado para usar o header `event-type` como identificador do tipo, mapeando `"CancelWorkOrderIntegrationCommand"` para a classe `CancelWorkOrderIntegrationCommand` e assim por diante. Ele lê o corpo diretamente no tipo do integration command e ignora campos desconhecidos, então o remetente pode acrescentar campos sem quebrar o consumer. Header ausente ou `event-type` desconhecido viram `MessageConversionException`, e a mensagem vai para a DLQ sem tentativas. O `ObjectMapper` vem de `MessageJsonSerializer.newObjectMapper()`, o mesmo usado para escrever as mensagens na outbox.
- **`WorkOrderConsumer` (infra/features/workorder/amqp):** o único consumer. Tem o `@RabbitListener` na classe, apontando para a fila `workorder`, e um método `@RabbitHandler` por command. O Spring escolhe o método pelo tipo do objeto convertido. Cada método só chama `consume(command, message, channel, useCase::execute)`.
- **`MessageConsumer` (infra):** a classe base, o padrão acordado: **qualquer consumer estende dela**. Ela controla o fluxo do inbox, a transação, o ack, a recusa, o registro de falha e a DLQ. O filho só informa qual use case chamar.
- **`MessageConsumerSupport` (infra):** reúne os colaboradores comuns (inbox e transação).
- **Topologia:** a fila, a DLQ e os binds ficam em `messaging/workorder-definitions.json`, e o nome da fila vem de `messaging.queue.workorder`.

### Por que o ack só depois do commit

Se o ack fosse dado **antes** do commit e a aplicação caísse no meio, o command **se perderia**: o RabbitMQ já apagou e o banco não gravou. Com o ack **depois**, o pior caso é cair entre o commit e o ack. A mensagem volta, e o **inbox** reconhece o `messageId` e a ignora. As duas peças andam juntas: o ack tardio cria a possibilidade de duplicata, e o inbox a neutraliza.

### O que acontece em cada situação

| Situação | O que acontece | Ack? |
|---|---|---|
| **Caso normal** | Inbox, use case e outbox no mesmo commit. | Sim |
| **Duplicata** (`messageId` já no inbox) | Não executa nada. | Sim |
| **Recusa de negócio** (ex.: `Authorize` em OS já cancelada; o use case lança exceção de domínio) | A transação 1 é desfeita (nada fica pela metade). Uma **segunda transação** registra o `messageId` no inbox com `status = REJECTED`. **Nenhum evento é publicado**: o saga não é avisado da recusa. Um reenvio do mesmo `messageId` é **reprocessado**. | Sim |
| **OS não existe** | Igual à recusa de negócio. | Sim |
| **Falha técnica** (banco fora do ar, bug inesperado) | **Sem nova tentativa.** A transação é desfeita, o inbox registra a mensagem com `status = FAILED` e o motivo, e a mensagem vai para a DLQ. | Nack |
| **Mensagem inválida** (JSON ilegível, sem o header `message-id`, sem `workOrderId`, sem header `event-type` ou com tipo desconhecido) | Não dá para registrar no inbox sem `messageId`. Vai direto para a DLQ. | Nack |

**Por que a recusa de negócio recebe ack?** Não é erro técnico: o sistema funcionou e decidiu "não". Repetir a mesma mensagem daria o mesmo "não". Por enquanto não existe evento de recusa, então quem enviou o command só descobre olhando o inbox ou os logs.

**Por que duas transações na recusa?** Quando o use case falha, a transação inteira é desfeita, inclusive o registro do inbox. É preciso uma nova transação para deixar a recusa registrada como `REJECTED`, o que mantém o histórico de que a mensagem chegou e não foi aplicada.

### Falha técnica, status FAILED e DLQ

Não existe retry. Na primeira falha técnica, o consumer:

1. Desfaz a transação do processamento (nenhum efeito pela metade).
2. Numa nova transação, grava no inbox a mensagem com **`status = FAILED`** e o motivo em `error`.
3. Dá `nack` **sem devolver à fila**, e o RabbitMQ envia a mensagem para a **DLQ**.

Na DLQ ela fica guardada. Se alguém a reenviar, o inbox permite o reprocessamento: uma mensagem com `status = FAILED` ou `REJECTED` não é tratada como duplicata, e passa a `PROCESSED` (ou volta a `REJECTED`) conforme o resultado. Ao reprocessar, o inbox atualiza também o tipo e a OS da mensagem. Só as mensagens `PROCESSED` são ignoradas como duplicatas.

Status do inbox: `PROCESSED` (efeito aplicado, não reprocessa), `REJECTED` (recusa de negócio, reprocessa) e `FAILED` (falha técnica, reprocessa).

### Uma fila e um consumer: efeito na ordem

A fila `workorder` tem um único consumer (`concurrency=1`), então **todos os commands são processados na ordem de chegada**, inclusive entre tipos diferentes. Um `Authorize` e um `Release` da mesma OS são processados na ordem em que chegaram.

Para escalar no futuro sem perder a ordem por OS, o caminho é particionar por `hash(workOrderId) % N`, com N filas e um consumer cada.

---

## 7. Commands e eventos

### Commands que a workorder-api recebe

| `event-type` | Campos próprios | O que a OS faz |
|---|---|---|
| `CancelWorkOrderIntegrationCommand` | nenhum | Cancela (só antes da execução). Se já está cancelada, não faz nada e não emite evento. |
| `AuthorizeWorkOrderIntegrationCommand` | nenhum | `DIAGNOSED` → `AWAITING_EXECUTION` |
| `ChangesRequestedWorkOrderIntegrationCommand` | `observation` | Vai para `CHANGES_REQUESTED` (editável de novo) |
| `ReleaseWorkOrderVehicleIntegrationCommand` | nenhum | `EXECUTION_COMPLETED` → `PAID` |

### Eventos que a workorder-api publica

Todos levam `workOrderId` no corpo. A coluna mostra o que cada um acrescenta.

| `event-type` | Quando | Campos próprios |
|---|---|---|
| `WorkOrderCreatedEvent` | OS criada | dados da OS direto no evento (veja abaixo) |
| `WorkOrderUpdatedEvent` | OS ou itens alterados | dados da OS direto no evento (veja abaixo) |
| `WorkOrderDiagnosedEvent` | Diagnóstico feito | nenhum |
| `WorkOrderAuthorizedEvent` | Command `Authorize` aceito | nenhum |
| `WorkOrderChangesRequestedEvent` | Command `RequestChanges` aceito | `observation` |
| `WorkOrderExecutionStartedEvent` | Execução iniciada | nenhum |
| `WorkOrderExecutionFinishedEvent` | Execução concluída | nenhum |
| `WorkOrderVehicleReleasedEvent` | Command `Release` aceito | nenhum |
| `WorkOrderVehicleDeliveredEvent` | Veículo entregue | nenhum |
| `WorkOrderCanceledEvent` | OS cancelada | nenhum |

#### Os dados da OS nos eventos Created e Updated

Ficam direto no corpo do evento, sem um objeto `workOrder` por fora. Levam tudo o que é necessário para gerar o orçamento, sem precisar consultar outro serviço:

```json
{
  "workOrderId": "...", "description": "...", "status": "RECEIVED",
  "client": { "id": "...", "name": "Frota ABC Logística", "email": "contato@abc-logistica.com",
              "document": "79013046002", "phone": "48999999001" },
  "vehicle": { "licensePlate": "ABC-1234", "model": "Volvo FH 460 - Caminhão", "brand": "Volvo", "modelYear": 2020 },
  "executionStartAt": null, "executionEndAt": null,
  "laborItems": [
    { "id": "...", "laborId": "...", "name": "Troca de óleo", "price": 150.00,
      "status": "AWAITING_EXECUTION", "executionStartAt": null, "executionEndAt": null }
  ],
  "materialItems": [
    { "id": "...", "materialId": "...", "name": "Filtro de óleo", "brand": "Bosch", "description": "...",
      "type": "PART", "unitPrice": 40.00, "quantity": 2, "totalPrice": 80.00 }
  ]
}
```

Os dados de cliente, veículo, mão de obra e material são os do cadastro **no momento da publicação**.

Esses eventos são uma cópia dos dados da OS naquele instante: cliente, veículo, descrição, status, tempos, as listas de mão de obra e material (`LaborItemSnapshot` e `MaterialItemSnapshot`, com ids, nomes, preços e quantidades). Os totais **não** são calculados: quem gera o orçamento soma.

---

## 8. Exemplo completo

Cenário: o saga quer **autorizar** uma OS diagnosticada, e a aplicação cai no pior momento.

1. O saga publica na `saga-output` (que repassa à `workorder-input`) com `event-type = AuthorizeWorkOrderIntegrationCommand`, `message-id = M1`, para a OS `X`.
2. O header casa com um dos binds da fila `workorder`. O conversor lê o `event-type`, monta o `AuthorizeWorkOrderIntegrationCommand`, e o Spring chama `WorkOrderConsumer.onAuthorize`.
3. Numa transação: o inbox registra `M1`; o use case muda a OS para `AWAITING_EXECUTION` e grava o `WorkOrderAuthorizedEvent` na outbox; commit.
4. **A aplicação cai agora**, antes do ack.
5. O RabbitMQ reentrega a mensagem `M1`.
6. O consumer chama `process`. O inbox já tem `M1`: **duplicata**. Nada é executado e o consumer dá ack. A OS **não** é autorizada duas vezes.
7. Enquanto isso, o `OutboxPublisher` encontra o evento pendente, publica na `workorder-output` com `event-type = WorkOrderAuthorizedEvent`, espera o confirm e marca `published_at`.
8. As filas que fizeram bind nesse `event-type` recebem o evento.

Se no passo 3 a OS já estivesse cancelada, a transação seria desfeita e, em outra transação, `M1` seria registrado no inbox como `REJECTED`. Nenhum evento seria publicado, e um reenvio de `M1` seria reprocessado.

---

## 9. Mapa do código

```
core/
  shared/messaging/
    MessagePublisher           porta genérica de publicação
  workorder/
    domain/events/*            cada evento é um record
    usecase/*                  regras de negócio; publicam eventos
    usecase/command/*          commands dos use cases

infra/messaging/
  serialization/               MessageJsonSerializer
  outbox/                      OutboxMessagePublisher (grava), OutboxPublisher (publica),
                               RabbitOutboxMessageSender
  inbox/                       InboxJpaEntity, InboxJpaRepository
  consumer/                    MessageConsumer (base), MessageConsumerSupport,
                               ProcessingResult, InvalidMessageException,
                               CommandConsumerProperties
  config/                      RabbitMQConfig (conversor por event-type, template, container),
                               MessagingConfig (agendamento e propriedades)

infra/features/workorder/
  api/                         controllers REST
  amqp/                        WorkOrderConsumer (um @RabbitHandler por command)

infra/config/TransactionalProxy      abre a transação em volta dos use cases
db/migration/                        V10 outbox · V11 inbox · V12 revisão do diagnóstico
```

### Como criar um novo consumer

1. Criar o command e o use case no core.
2. Criar o integration command (`record` com `toCommand()`) em `infra/features/workorder/amqp/command`.
3. Acrescentá-lo à lista `COMMANDS` do `RabbitMQConfig` (mapeamento do conversor) e um bind por `event-type` em `messaging/workorder-definitions.json`.
4. Acrescentar um método `@RabbitHandler` no `WorkOrderConsumer` chamando `consume(command.workOrderId(), command, message, channel, received -> useCase.execute(received.toCommand()))`.

---

## 10. Configurações (variáveis de ambiente)

| Variável | Padrão | Efeito |
|---|---|---|
| `OUTBOX_SCHEDULER_ENABLED` | `true` | Liga ou desliga a publicação da outbox |
| `OUTBOX_PUBLISHER_INTERVAL_MS` | `1000` | Intervalo entre execuções |
| `OUTBOX_BATCH_SIZE` | `50` | Linhas por lote |
| `OUTBOX_CONFIRM_TIMEOUT_MS` | `5000` | Quanto esperar o confirm do RabbitMQ |
| `OUTBOX_RETENTION_DAYS` | `7` | Dias antes de apagar as já publicadas |
| `OUTBOX_CLEANUP_CRON` | `0 0 3 * * *` | Horário da limpeza |
| `CONSUMER_PREFETCH` | `10` | Mensagens que o consumer recebe adiantadas |

O nome da exchange de saída fica em `messaging.exchange.workorder-output` (padrão: `workorder-output`).

---

## 11. Garantias e limites

**O que o desenho garante**
- Nenhum evento se perde: ele está no banco junto com a mudança da OS.
- Nenhum command é executado duas vezes: o inbox bloqueia a duplicata.
- Nenhum efeito pela metade: inbox, OS e outbox são confirmados juntos.
- Nenhum command se perde: o ack vem depois do commit, e uma falha vai para a DLQ com o inbox em `FAILED`.
- Ordem dos eventos: a `sequence` da outbox.

**O que o desenho NÃO faz**
- **Quem recebe um evento pode recebê-lo mais de uma vez.** Por isso o `messageId`: o receptor precisa deduplicar do lado dele, como fazemos aqui com o inbox.
- **A fila `workorder` tem um consumer só**, então o throughput de commands é limitado a uma thread. Para o tamanho atual do serviço isso é suficiente.
- **Não há `version` na mensagem.** O receptor não tem como descartar um evento velho que chegue atrasado, e duas escritas simultâneas na mesma OS não são mais barradas por uma constraint. Se for preciso, o caminho é um `@Version` otimista na entidade.
- **Sem retry, uma falha momentânea vira definitiva.** Se o RabbitMQ estiver fora do ar na hora da publicação, as mensagens daquele ciclo ficam `FAILED` na outbox e **não são reenviadas sozinhas**. É preciso reenviá-las manualmente (por exemplo, voltando o `status` para `PENDING`). O mesmo vale para um command que falha por problema técnico: fica `FAILED` no inbox e na DLQ até alguém reenviá-lo.
- **Um evento posterior pode ser publicado mesmo que um anterior da mesma OS tenha ficado `FAILED`**, então a ordem pode ser quebrada nesse caso.
- **Command para uma OS que não existe não gera resposta**, porque não há OS para atribuir o evento.
