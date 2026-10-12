CREATE TABLE IF NOT EXISTS work_orders (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    vehicle_id VARCHAR(255) NOT NULL,
    description TEXT,
    status INTEGER NOT NULL,
    execution_start_at TIMESTAMP,
    execution_end_at TIMESTAMP,
    date_created TIMESTAMP NOT NULL,
    date_updated TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_work_orders_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT fk_work_orders_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(license_plate)
);

CREATE TABLE IF NOT EXISTS work_order_labor_items (
    id UUID PRIMARY KEY,
    work_order_id UUID NOT NULL,
    labor_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    execution_start_at TIMESTAMP,
    execution_end_at TIMESTAMP,
    CONSTRAINT fk_work_order_labor_items_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    CONSTRAINT fk_labor FOREIGN KEY (labor_id) REFERENCES labors(id)
);

CREATE TABLE IF NOT EXISTS work_order_material_items (
    id UUID PRIMARY KEY,
    work_order_id UUID NOT NULL,
    material_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    CONSTRAINT fk_work_order_material_items_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    CONSTRAINT fk_material FOREIGN KEY (material_id) REFERENCES materials(id)
);

CREATE TABLE IF NOT EXISTS work_order_budgets (
    work_order_id UUID PRIMARY KEY,
    total_price DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_work_order_budgets_work_order FOREIGN KEY (work_order_id) REFERENCES work_orders(id)
);

CREATE INDEX IF NOT EXISTS idx_work_orders_status ON work_orders(status);
CREATE INDEX IF NOT EXISTS idx_work_orders_client_id ON work_orders(client_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_vehicle_id ON work_orders(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_work_order_labor_items_work_order_id ON work_order_labor_items(work_order_id);
CREATE INDEX IF NOT EXISTS idx_work_order_material_items_work_order_id ON work_order_material_items(work_order_id);
CREATE INDEX IF NOT EXISTS idx_work_order_budgets_work_order_id ON work_order_budgets(work_order_id);
