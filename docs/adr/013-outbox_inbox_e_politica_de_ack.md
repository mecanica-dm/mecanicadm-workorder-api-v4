# [013]: [Outbox, inbox e política de ack no consumo de commands]

* **Status**: Aceito
* **Data**: 12/11/2026

## 1. Contexto e Problema

A Workorder troca mensagens com a saga pelo RabbitMQ, que entrega cada mensagem **pelo menos uma vez**. Isso cria dois
problemas: salvar no banco e publicar não são uma operação só (a OS muda, mas ninguém fica sabendo), e a mesma mensagem pode
chegar duas vezes (um command seria executado duas vezes). Também é preciso decidir quando confirmar (ack) uma mensagem
e o que fazer quando o processamento falha.

A arquitetura está em [arquitetura-da-mensageria.md](../apoio/arquitetura-da-mensageria.md). Esta ADR registra as decisões.

## 2. Decisão

### 2.1 Outbox transacional (saída)

O evento é gravado na tabela `outbox`, **na mesma transação** da mudança da OS. Um publicador agendado envia as
pendentes ao RabbitMQ, na ordem da coluna `sequence`, e só marca `PUBLISHED` depois do *publisher confirm* do broker.

- A publicação exige transação ativa (`Propagation.MANDATORY`): publicar fora de uma transação é erro.
- Status: `PENDING`, `PUBLISHED` e `FAILED` (falhou, sem nova tentativa, com o motivo em `last_error`).
- As mensagens publicadas são limpas por uma rotina periódica.

### 2.2 Inbox idempotente (entrada)

Cada command é registrado na tabela `inbox` pela chave `message-id` (header), **na mesma transação** que executa o caso de
uso, com `INSERT ... ON CONFLICT`. Status: `PROCESSED`, `REJECTED` e `FAILED`. Só `PROCESSED` bloqueia um reenvio.

### 2.3 Política de ack

| Situação | Reação | Ack |
|---|---|---|
| Caso normal | Inbox, caso de uso e outbox no mesmo commit | Ack |
| Duplicata (`PROCESSED`) | Não executa | Ack |
| Recusa de negócio ou OS inexistente | Transação desfeita, `REJECTED` no inbox e `WorkOrderCommandRejectedEvent` (ADR 012) | Ack |
| Falha técnica | Sem nova tentativa: transação desfeita, `FAILED` no inbox | Nack, vai para a DLQ |
| Mensagem inválida (JSON ilegível, sem `message-id`, `workOrderId` ou `event-type`, ou tipo desconhecido) | Não registra no inbox | Nack, vai para a DLQ |

O ack só acontece **depois do commit**. Falha técnica **não é repetida** automaticamente: vai para a DLQ para uma pessoa
(ou um reprocessamento deliberado, com o mesmo `message-id`) decidir.

### 2.4 Um consumer por fila

A fila `workorder` tem um único consumer, com *prefetch* configurável, o que preserva a ordem de tratamento dos commands.

## 3. Consequências

### Positivas

- Nenhum evento se perde, e nenhum sai sem a mudança correspondente gravada.
- Command repetido não executa duas vezes.
- O destino de cada mensagem é previsível e fica registrado no inbox.

### Negativas / Custos

- Latência extra: o evento sai pelo publicador, e não na hora.
- Falha técnica exige intervenção (olhar a DLQ), em vez de uma nova tentativa automática.
- Mais duas tabelas e a limpeza periódica da outbox.

## 4. Relação com Outras Decisões

- [ADR 012 - Evento de recusa de command](012-evento_de_recusa_de_command.md)
- ADR 003 da Saga API e ADR 003 da Finance API: o mesmo padrão nos outros serviços.
