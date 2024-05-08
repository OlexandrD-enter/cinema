CREATE TABLE IF NOT EXISTS cinema_service.tickets
(
    id                BIGSERIAL    NOT NULL,
    showtime_id       BIGINT       NOT NULL,
    room_seat_id      BIGINT       NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT tickets_showtime_id_fk
        FOREIGN KEY (showtime_id) REFERENCES cinema_service.showtimes (id)
            ON DELETE CASCADE,
    CONSTRAINT tickets_room_seat_id_fk
        FOREIGN KEY (room_seat_id) REFERENCES cinema_service.room_seats (id)
            ON DELETE CASCADE
);