# RFC 003 - Histórico de Status da Ordem de Serviço

* **Status**: Aprovado (implementado em 12/11/2026)
* **Data**: 12/11/2026
* **Autores**: Time de desenvolvimento
* **Resumo**: Este documento propõe registrar **cada mudança de status da OS** e expô-la por um endpoint de consulta, para
  atender ao requisito do desafio de "consulta de status e histórico".

---

## 1. Resumo

O desafio atribui ao serviço de OS a "consulta de status e histórico". Hoje a Workorder expõe o **status atual**
(`GET /work-orders/{id}/status`) e não guarda as mudanças anteriores. O histórico do **fluxo** existe na saga
(`GET /api/sagas/{workOrderId}`), mas é o histórico do processo distribuído, e não o das transições de status da OS.

## 2. Contexto

- A OS tem o ciclo `RECEIVED`, `DIAGNOSED`, `AWAITING_EXECUTION`, `IN_EXECUTION`, `EXECUTION_COMPLETED`, `PAID`,
  `DELIVERED`, além de `CHANGES_REQUESTED` e `CANCELLED`.
- Cada transição é um método da entidade `WorkOrder` (por exemplo, `markAsDiagnosed`, `markAsInExecution`), e todo caso de
  uso grava a OS pelo gateway (`update`).
- O histórico responderia: quando a OS foi diagnosticada, quando foi autorizada, quanto tempo ficou em cada status e por
  que voltou a `CHANGES_REQUESTED`.

## 3. Objetivos

- Registrar cada mudança de status com a data, o status de origem e de destino e o motivo (quando houver).
- Consultar o histórico de uma OS por um endpoint.
- Não duplicar regra de negócio nem criar um segundo caminho para mudar o status.

## 4. Alternativas Consideradas

### 4.1. Tabela `work_order_status_history`, gravada no gateway

Uma linha por mudança, gravada pelo gateway quando o status da OS difere do anterior.

**Prós:** cobre todos os casos de uso sem alterá-los; não depende de lembrar de registrar em cada um.

**Contras:** a regra "o que é uma mudança" fica na infraestrutura, e não no domínio (contraria o ADR 011);
o motivo (por exemplo, a observação do pedido de alterações) não está à mão no gateway.

### 4.2. A entidade registra as mudanças, e o gateway as persiste

A `WorkOrder` mantém uma lista de `StatusChange` preenchida pelos métodos de transição (`markAs...`), e o gateway grava as
novas linhas ao salvar.

**Prós:** a regra fica no domínio, no mesmo lugar da validação da transição; o motivo vai junto;
alinhado ao ADR 011.

**Contras:** altera a entidade e a persistência; todo método de transição precisa registrar (coberto por teste).

### 4.3. Derivar o histórico dos eventos já publicados (outbox)

Consultar a tabela `outbox` e reconstruir as mudanças a partir dos eventos.

**Prós:** nenhuma tabela nova.

**Contras:** a outbox é limpa periodicamente, não guarda todos os status (nem todo status gera evento) e mistura
infraestrutura com consulta de negócio.

### 4.4. Usar só o histórico da saga

**Prós:** já existe e está pronto.

**Contras:** é o histórico do fluxo distribuído (eventos e commands), não das transições da OS; não cobre mudanças feitas
por chamadas diretas à API (por exemplo, iniciar a execução).

## 5. Proposta

Implementar a **alternativa 4.2**:

| Aspecto | Proposta |
|---|---|
| Tabela | `work_order_status_history` (`id`, `work_order_id`, `from_status`, `to_status`, `reason`, `changed_by`, `changed_at`), **sem chave estrangeira** para `work_orders` |
| Migração | `V14`, que também cria uma linha inicial, com o status atual, para cada OS existente (autor `migration`) |
| Domínio | `WorkOrder` registra um `WorkOrderStatusChange` em cada transição (`changeStatus`) e na criação (de nada para `RECEIVED`); o gateway pega e grava as mudanças com `pullStatusChanges()` |
| Consulta | `GET /work-orders/{id}/history`, em ordem cronológica, com o caso de uso `GetWorkOrderHistoryUseCase` (nomenclatura do ADR 001); 404 se a OS não existir |
| Quem mudou | O e-mail do usuário autenticado, quando a mudança vem da API; `saga` quando vem de um command |

## 6. Impacto e Riscos

- **Risco**: uma transição nova que esqueça de registrar a mudança.
    - **Mitigação**: o registro fica em um método único usado por todas as transições, com teste que percorre todas.
- **Risco**: OS antigas sem histórico.
    - **Mitigação**: a migration cria uma linha inicial com o status atual.
- **Risco**: crescimento da tabela.
    - **Mitigação**: o volume é de poucas linhas por OS; índice por `work_order_id`.

## 7. Implementação

1. Migration `V14` e a entidade JPA `WorkOrderStatusHistoryJpaEntity`. (feito)
2. `WorkOrderStatusChange` na entidade `WorkOrder` e a gravação no `WorkOrderRepositoryImpl` (`create` e `update`), na mesma transação da OS. (feito)
3. `GetWorkOrderHistoryUseCase`, `WorkOrderHistoryController` e o DTO `WorkOrderStatusChangeResponse`. (feito)
4. Testes: `WorkOrderStatusHistoryTest` (todas as transições, motivo, cancelamento repetido, transição recusada), `GetWorkOrderHistoryUseCaseTest`, `WorkOrderHistoryControllerTest` e `WorkOrderStatusHistoryIT` (autor `saga` e usuário autenticado, endpoint, 404 e 401). (feito)

## 8. Decisão Final

| Item      | Detalhe                                        |
|-----------|------------------------------------------------|
| Histórico | Registrado pela entidade e persistido em tabela |
| Status    | Proposta — aprovada e implementada             |
