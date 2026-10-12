CREATE INDEX IF NOT EXISTS idx_work_orders_execution_end_deleted
    ON work_orders (execution_end_at, deleted_at);

CREATE INDEX IF NOT EXISTS idx_work_order_labor_items_execution_end
    ON work_order_labor_items (execution_end_at);

