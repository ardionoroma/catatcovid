CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(25) NOT NULL,
    password VARCHAR(128) NOT NULL,
    last_logged_in TIMESTAMP NOT NULL,
    alert_meter NUMERIC(7,3) NOT NULL,
    district_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE users ADD CONSTRAINT fk_users_district_id FOREIGN KEY (district_id) REFERENCES districts(id);
