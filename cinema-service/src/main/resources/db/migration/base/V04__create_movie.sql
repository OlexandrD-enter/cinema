CREATE TABLE IF NOT EXISTS cinema_service.movies
(
    id                BIGSERIAL           NOT NULL,
    name              VARCHAR(255) UNIQUE NOT NULL,
    description       TEXT                NOT NULL,
    age_limit         INTEGER             NOT NULL,
    language          VARCHAR(100)        NOT NULL,
    country           VARCHAR(100)        NOT NULL,
    director          VARCHAR(255)        NOT NULL,
    realise_date      TIMESTAMP           NOT NULL,
    created_at        TIMESTAMP(6)        NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255)        NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id)
);