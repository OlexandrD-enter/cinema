CREATE TABLE IF NOT EXISTS cinema_service.showtimes
(
    id                BIGSERIAL    NOT NULL,
    movie_id          BIGINT       NOT NULL,
    room_id           BIGINT       NOT NULL,
    start_date        TIMESTAMP(6) NOT NULL,
    end_date          TIMESTAMP(6) NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT showtimes_movie_id_fk
        FOREIGN KEY (movie_id) REFERENCES cinema_service.movies (id)
            ON DELETE CASCADE,
    CONSTRAINT showtimes_room_id_fk
        FOREIGN KEY (room_id) REFERENCES cinema_service.cinema_rooms (id)
            ON DELETE CASCADE
);