-- Transactional Outbox: o evento é gravado na mesma transação da mudança de estado da OS.
-- id = messageId (chave de idempotência do consumidor).
-- type = eventType da mensagem (nome da classe); vai como header AMQP e é por ele que as filas fazem o bind.
-- status: PENDING (aguardando), PUBLISHED (confirmada pelo broker) ou FAILED (falhou, sem nova tentativa; last_error guarda o motivo).
-- payload = a mensagem completa em JSON (cabeçalho + campos próprios).
CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    sequence BIGSERIAL NOT NULL,
    aggregate_id UUID NOT NULL,
    type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    published_at TIMESTAMPTZ,
    last_error TEXT,
    CONSTRAINT uq_outbox_sequence UNIQUE (sequence)
);

-- Índices parciais: o publisher só olha para as pendentes.
CREATE INDEX IF NOT EXISTS idx_outbox_pending ON outbox (sequence) WHERE status = 'PENDING';
-- Usado pela rotina de limpeza das já publicadas.
CREATE INDEX IF NOT EXISTS idx_outbox_published_at ON outbox (published_at) WHERE status = 'PUBLISHED';
