CREATE TABLE IF NOT EXISTS cinema_service.cinemas
(
    id                BIGSERIAL    NOT NULL,
    name              VARCHAR(255) UNIQUE NOT NULL,
    city              VARCHAR(255) NOT NULL,
    street_address    VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id)
);