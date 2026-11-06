CREATE TABLE IF NOT EXISTS stock_movements (
    id UUID PRIMARY KEY,
    material_id UUID NOT NULL,
    work_order_id UUID,
    quantity INTEGER NOT NULL DEFAULT 0,
    type VARCHAR(50) NOT NULL,
    date_created TIMESTAMP NOT NULL,
    date_updated TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_stock_material FOREIGN KEY (material_id) REFERENCES materials(id),
    CONSTRAINT fk_stock_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders(id)
);
