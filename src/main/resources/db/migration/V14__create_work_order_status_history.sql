CREATE TABLE IF NOT EXISTS work_order_status_history (
    id BIGSERIAL PRIMARY KEY,
    work_order_id UUID NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    reason TEXT,
    changed_by VARCHAR(255) NOT NULL,
    changed_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_work_order_status_history_work_order
    ON work_order_status_history (work_order_id, changed_at, id);

INSERT INTO work_order_status_history (work_order_id, from_status, to_status, reason, changed_by, changed_at)
SELECT id,
       NULL,
       CASE status
           WHEN 0 THEN 'IN_EXECUTION'
           WHEN 1 THEN 'AWAITING_EXECUTION'
           WHEN 2 THEN 'DIAGNOSED'
           WHEN 3 THEN 'RECEIVED'
           WHEN 4 THEN 'EXECUTION_COMPLETED'
           WHEN 5 THEN 'PAID'
           WHEN 6 THEN 'DELIVERED'
           WHEN 7 THEN 'CHANGES_REQUESTED'
           ELSE 'CANCELLED'
       END,
       'Historico iniciado na migracao',
       'migration',
       COALESCE(date_updated, date_created)
FROM work_orders;
