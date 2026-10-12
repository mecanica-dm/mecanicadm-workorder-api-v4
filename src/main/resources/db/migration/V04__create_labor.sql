CREATE TABLE IF NOT EXISTS labors (
    id           UUID PRIMARY KEY,
    name         VARCHAR(255)   NOT NULL,
    price        DECIMAL(19, 2) NOT NULL,
    date_created TIMESTAMP      NOT NULL,
    date_updated TIMESTAMP      NOT NULL,
    deleted_at   TIMESTAMP
);