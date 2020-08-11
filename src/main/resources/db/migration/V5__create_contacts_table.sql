CREATE TABLE IF NOT EXISTS contacts
(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    is_healthy BOOLEAN NOT NULL DEFAULT TRUE,
    is_cough_sneeze BOOLEAN NOT NULL DEFAULT FALSE,
    is_masked BOOLEAN NOT NULL DEFAULT TRUE,
    is_crowded BOOLEAN NOT NULL DEFAULT FALSE,
    is_phydist BOOLEAN NOT NULL DEFAULT TRUE,
    contact_date DATE NOT NULL,
    score NUMERIC(7,3) NOT NULL,
    district_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE contacts ADD CONSTRAINT fk_districts_user_id FOREIGN KEY (district_id) REFERENCES districts(id);
ALTER TABLE contacts ADD CONSTRAINT fk_contacts_user_id FOREIGN KEY (user_id) REFERENCES users(id);
