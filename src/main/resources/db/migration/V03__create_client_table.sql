CREATE TABLE IF NOT EXISTS clients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    document VARCHAR(18) NOT NULL UNIQUE,
    phone VARCHAR(20),
    date_created TIMESTAMP NOT NULL,
    date_updated TIMESTAMP NOT NULL,
    deleted_at TIMESTAMP
);
