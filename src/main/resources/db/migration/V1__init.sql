-- job_position (not "position", which is a reserved word in MySQL 8.0+ and Oracle)
-- AUTO_INCREMENT is understood by both MySQL and H2 (H2 accepts it as an alias for IDENTITY).
CREATE TABLE employees (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name           VARCHAR(255) NOT NULL,
    middle_name          VARCHAR(255),
    paternal_last_name   VARCHAR(255) NOT NULL,
    maternal_last_name   VARCHAR(255),
    age                  INT NOT NULL,
    sex                  VARCHAR(20) NOT NULL,
    birth_date           DATE NOT NULL,
    job_position         VARCHAR(255) NOT NULL,
    registration_date    TIMESTAMP NOT NULL,
    active               BOOLEAN NOT NULL
);
