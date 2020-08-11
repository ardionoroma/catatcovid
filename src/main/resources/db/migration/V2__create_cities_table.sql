CREATE TABLE IF NOT EXISTS cities
(
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    province_id SMALLINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE cities ADD CONSTRAINT fk_cities_province_id FOREIGN KEY (province_id) REFERENCES provinces(id);
