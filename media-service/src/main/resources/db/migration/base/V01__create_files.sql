CREATE TABLE IF NOT EXISTS media_service.files
(
    id                BIGSERIAL    NOT NULL,
    name              VARCHAR(255) NOT NULL unique,
    target_id         BIGINT       NOT NULL,
    target_type       VARCHAR(255) NOT NULL,
    mime_type         VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    primary key (id)
);