CREATE TABLE IF NOT EXISTS users_service.user_tokens
(
    id           BIGSERIAL    NOT NULL,
    token        VARCHAR(255) NOT NULL UNIQUE,
    token_type   VARCHAR(255) NOT NULL,
    user_id      BIGINT       NOT NULL,
    is_used      BOOLEAN      NOT NULL,
    last_send_at TIMESTAMP(6) NOT NULL,
    created_at   TIMESTAMP(6) NOT NULL,
    updated_at   TIMESTAMP(6),
    PRIMARY KEY (id)
);

ALTER TABLE users_service.user_tokens
    ADD CONSTRAINT user_tokens_user_id FOREIGN KEY (user_id) REFERENCES users_service.users (id) ON DELETE CASCADE;

ALTER TABLE users_service.user_tokens
    ADD CONSTRAINT unique_user_by_token_type UNIQUE (user_id, token_type);