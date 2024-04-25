CREATE TABLE IF NOT EXISTS cinema_service.room_seats
(
    id                BIGSERIAL    NOT NULL,
    seat_number       BIGINT       NOT NULL,
    room_id           BIGINT       NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT room_seats_room_id_fk
        FOREIGN KEY (room_id) REFERENCES cinema_service.cinema_rooms (id)
            ON DELETE CASCADE,
    CONSTRAINT unique_room_seat_number
        UNIQUE (seat_number, room_id)
);