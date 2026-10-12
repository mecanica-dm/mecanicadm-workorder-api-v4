CREATE TABLE IF NOT EXISTS materials (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255),
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    date_created TIMESTAMP NOT NULL,
    date_updated TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_materials_name ON materials(name);
CREATE INDEX IF NOT EXISTS idx_materials_brand ON materials(brand);
