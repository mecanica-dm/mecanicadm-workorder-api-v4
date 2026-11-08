CREATE TABLE IF NOT EXISTS vehicle (
    license_plate VARCHAR(255) PRIMARY KEY,
    model VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    model_year SMALLINT NOT NULL,
    date_created TIMESTAMP,
    date_updated TIMESTAMP,
    deleted_at TIMESTAMP
);
