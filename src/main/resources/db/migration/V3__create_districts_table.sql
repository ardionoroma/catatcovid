CREATE TABLE IF NOT EXISTS districts
(
    id INT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    alert_meter NUMERIC(7,3) NOT NULL,
    city_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE districts ADD CONSTRAINT fk_districts_city_id FOREIGN KEY (city_id) REFERENCES cities(id);
