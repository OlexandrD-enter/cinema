CREATE TABLE IF NOT EXISTS cinema_service.order_payment_details
(
    id             BIGSERIAL NOT NULL,
    order_id       BIGINT UNIQUE,
    transaction_id VARCHAR(255) UNIQUE,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT order_payment_details_order_id_fk
        FOREIGN KEY (order_id) REFERENCES cinema_service.orders (id)
            ON DELETE CASCADE
);