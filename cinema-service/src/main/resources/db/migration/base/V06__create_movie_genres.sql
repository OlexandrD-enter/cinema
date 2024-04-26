CREATE TABLE IF NOT EXISTS cinema_service.movie_genres
(
    id                BIGSERIAL    NOT NULL,
    movie_id          BIGINT       NOT NULL,
    genre_id          BIGINT       NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT movie_genres_movie_id_fk
        FOREIGN KEY (movie_id) REFERENCES cinema_service.movies (id)
            ON DELETE CASCADE,
    CONSTRAINT movie_genres_genre_id_fk
        FOREIGN KEY (genre_id) REFERENCES cinema_service.genres (id)
            ON DELETE CASCADE
);