CREATE TABLE IF NOT EXISTS cinema_service.genres
(
    id                BIGSERIAL           NOT NULL,
    name              VARCHAR(255) UNIQUE NOT NULL,
    created_at        TIMESTAMP(6)        NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255)        NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id)
);