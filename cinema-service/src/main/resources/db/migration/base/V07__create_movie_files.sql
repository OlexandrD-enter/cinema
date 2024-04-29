CREATE TABLE IF NOT EXISTS cinema_service.movie_files
(
    id                BIGSERIAL    NOT NULL,
    movie_id          BIGINT       NOT NULL,
    file_id           BIGINT       NOT NULL,
    file_type         VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6),
    created_by_email  VARCHAR(255) NOT NULL,
    modified_by_email VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT movie_genres_movie_id_fk
        FOREIGN KEY (movie_id) REFERENCES cinema_service.movies (id)
            ON DELETE CASCADE
);