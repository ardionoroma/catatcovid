CREATE TABLE IF NOT EXISTS users
(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    username VARCHAR(25) NOT NULL,
    password VARCHAR(128) NOT NULL,
    is_logged_in BOOLEAN NOT NULL DEFAULT FALSE,
    last_logged_in TIMESTAMP NOT NULL,
    alert_meter NUMERIC(7,3) NOT NULL,
    district_id INT NOT NULL,
    refresh_token CHAR(20) NOT NULL DEFAULT '0NL0T29TTY9NTUBXNQEB',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE users ADD CONSTRAINT fk_users_district_id FOREIGN KEY (district_id) REFERENCES districts(id);
