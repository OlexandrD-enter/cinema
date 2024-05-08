CREATE TABLE IF NOT EXISTS cinema_service.orders
(
    id                BIGSERIAL    NOT NULL,
    status            VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id)
);