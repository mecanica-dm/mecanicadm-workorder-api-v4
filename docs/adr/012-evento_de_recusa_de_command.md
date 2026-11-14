# [012]: [Evento de recusa de command]

* **Status**: Aceito
* **Data**: 12/11/2026

## 1. Contexto e Problema

A saga envia commands à Workorder (autorizar, pedir alterações, cancelar, liberar o veículo) e espera um evento de
resposta. Quando a OS **recusa** o command por regra de negócio (por exemplo, autorizar uma OS que já está em execução, ou
uma OS que não existe), o consumer registrava `REJECTED` no inbox e dava ack, mas **não avisava ninguém**. A saga ficava
esperando uma resposta que não vinha, e o fluxo travava em silêncio.

Falha técnica é outro caso: a mensagem vai para a DLQ e pode ser reprocessada. Só a recusa de negócio precisa ser
comunicada.

## 2. Decisão

Quando um command é recusado por uma `DomainExceptionCore`, a Workorder publica o `WorkOrderCommandRejectedEvent`:

| Campo | Conteúdo |
|---|---|
| `workOrderId` | A OS do command recusado |
| `eventType` | O nome do command recusado (por exemplo, `AuthorizeWorkOrderIntegrationCommand`) |
| `exception` | O nome simples da exceção de domínio (por exemplo, `InvalidStatusTransition`) |
| `message` | O motivo, resolvido pelo `MessageSource` em pt-BR |

- O evento é gravado na **outbox, na mesma transação** que grava o `REJECTED` no inbox. Se a gravação falhar, nenhum
  dos dois acontece, e a mensagem é reprocessada.
- A recusa **continua dando ack**, porque não há nada a repetir: reenviar o mesmo command produziria a mesma recusa.
- Uma **duplicata** (mensagem já tratada) não emite o evento de novo. Um `REJECTED` reenviado com o mesmo `message-id` é
  reprocessado e, se for recusado outra vez, emite o evento outra vez.
- Falha técnica **não** gera evento: segue para a DLQ, com `FAILED` no inbox.
- A implementação está no `MessageConsumer`, que serve a todos os consumers de commands. O evento vive em
  `core/workorder/domain/events`.

A saga só reage se o command recusado for o que o estado atual dela espera (ADR 005 da Saga API).

## 3. Consequências

### Positivas

- A saga deixa de ficar presa esperando uma resposta que não virá e pode compensar ou pausar com o motivo registrado.
- Não há trabalho duplicado em cada caso de uso: o consumer base trata todos os commands do mesmo jeito.
- O motivo chega traduzido e legível, o que ajuda quem investiga uma pausa.

### Negativas / Custos

- A recusa passa a escrever na outbox, o que exige o consumer base conhecer o publicador e o `MessageSource`.
- O `message` vai sempre em pt-BR, independentemente do idioma de quem consulta a saga depois.
- O `eventType` é o nome da classe do command: renomear um command exige atualizar a saga.

## 4. Relação com Outras Decisões

- ADR 005 da Saga API (eventos de recusa) e ADR 004 da Finance API (o mesmo evento no Finance).
- [ADR 013 (outbox, inbox e política de ack)](013-outbox_inbox_e_politica_de_ack.md).
- [ADR 006 - Contrato de exceções de domínio e i18n](006-contrato_excecoes_dominio_i18n.md): é de lá que vem a chave de
  mensagem resolvida.
