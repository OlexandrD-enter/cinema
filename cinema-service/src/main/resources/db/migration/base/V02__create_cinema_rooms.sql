CREATE TABLE IF NOT EXISTS cinema_service.cinema_rooms
(
    id                BIGSERIAL    NOT NULL,
    name              VARCHAR(255) NOT NULL,
    room_type         VARCHAR(255) NOT NULL,
    cinema_id         BIGINT       NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT cinema_rooms_cinema_id_fk
        FOREIGN KEY (cinema_id) REFERENCES cinema_service.cinemas (id)
            ON DELETE CASCADE,
    CONSTRAINT unique_cinema_room_name
        UNIQUE (cinema_id, name)
);