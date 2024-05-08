CREATE TABLE IF NOT EXISTS cinema_service.order_tickets
(
    id                BIGSERIAL    NOT NULL,
    order_id       BIGINT       NOT NULL,
    ticket_id      BIGINT       NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT order_tickets_order_id_fk
        FOREIGN KEY (order_id) REFERENCES cinema_service.orders (id)
            ON DELETE CASCADE,
    CONSTRAINT order_ticket_id_fk
        FOREIGN KEY (ticket_id) REFERENCES cinema_service.tickets (id)
            ON DELETE CASCADE
);