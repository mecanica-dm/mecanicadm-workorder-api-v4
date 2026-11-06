-- Inbox: idempotência dos commands recebidos (at-least-once).
-- status: PROCESSED (efeito aplicado), REJECTED (recusado por regra de negócio) ou FAILED (falha técnica, sem nova tentativa; error guarda o motivo).
-- Uma mensagem FAILED pode ser reprocessada se for reenviada.
CREATE TABLE IF NOT EXISTS inbox (
    message_id UUID PRIMARY KEY,
    message_type VARCHAR(100) NOT NULL,
    work_order_id UUID,
    status VARCHAR(20) NOT NULL,
    error TEXT,
    processed_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_inbox_processed_at ON inbox (processed_at);
