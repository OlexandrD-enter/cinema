package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieCreateRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponse;
import com.project.cinemaservice.domain.mapper.MovieMapper;
import com.project.cinemaservice.persistence.enums.MovieFileType;
import com.project.cinemaservice.persistence.model.Genre;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.MovieFile;
import com.project.cinemaservice.persistence.model.MovieGenre;
import com.project.cinemaservice.persistence.repository.GenreRepository;
import com.project.cinemaservice.persistence.repository.MovieRepository;
import com.project.cinemaservice.service.MediaServiceClient;
import com.project.cinemaservice.service.MovieService;
import com.project.cinemaservice.service.execption.GenreAlreadyExistsException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * MovieService implementation responsible for movies related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

  private final MovieRepository movieRepository;
  private final GenreRepository genreRepository;
  private final MediaServiceClient mediaServiceClient;
  private final MovieMapper movieMapper;

  @Transactional
  @Override
  public MovieAdminResponse createMovie(MovieCreateRequest movieCreateRequest) {
    String movieName = movieCreateRequest.getName();

    log.debug("Creating movie with name {}", movieName);

    checkIfMovieExistByName(movieName);

    Movie movie = createMovieEntity(movieCreateRequest);

    saveMovieGenres(movie, movieCreateRequest.getMovieGenreIds());
    saveMovieFiles(movie, movieCreateRequest);

    log.debug("Movie created successfully with name {}", movieName);

    return movieMapper.toMovieAdminResponse(movie);
  }

  private void checkIfMovieExistByName(String movieName) {
    if (movieRepository.findByName(movieName).isPresent()) {
      throw new GenreAlreadyExistsException(
          String.format("Movie with name='%s' already exists", movieName));
    }
  }

  private Movie createMovieEntity(MovieCreateRequest movieCreateRequest) {
    Movie movie = movieMapper.toMovieEntity(movieCreateRequest);
    return movieRepository.save(movie);
  }

  private void saveMovieGenres(Movie movie, List<Long> genreIds) {
    List<Genre> genres = genreRepository.findAllById(genreIds);
    List<MovieGenre> movieGenres = genres.stream()
        .map(genre -> MovieGenre.builder()
            .genre(genre)
            .movie(movie)
            .build())
        .toList();

    movie.setMovieGenres(movieGenres);
  }

  private void saveMovieFiles(Movie movie, MovieCreateRequest movieCreateRequest) {
    MovieFileRequest previewFile = createMovieFileRequest(movie.getId(),
        MovieFileType.MOVIE_PREVIEW,
        movieCreateRequest.getPreviewImage());

    MovieFileResponse previewFileResponse = mediaServiceClient.uploadFile(previewFile,
        movieCreateRequest.getPreviewImage());

    MovieFileRequest trailerFile = createMovieFileRequest(movie.getId(),
        MovieFileType.MOVIE_TRAILER,
        movieCreateRequest.getTrailerVideo());

    MovieFileResponse trailerFileResponse = mediaServiceClient.uploadFile(trailerFile,
        movieCreateRequest.getTrailerVideo());

    MovieFile movieFilePreview = createMovieFile(movie, previewFileResponse);
    MovieFile movieFileTrailer = createMovieFile(movie, trailerFileResponse);

    movie.setMovieFiles(List.of(movieFilePreview, movieFileTrailer));
  }

  private MovieFileRequest createMovieFileRequest(Long movieId, MovieFileType fileType,
      MultipartFile file) {
    return MovieFileRequest.builder()
        .targetId(movieId)
        .targetType(fileType)
        .mimeType(file.getContentType())
        .build();
  }

  private MovieFile createMovieFile(Movie movie, MovieFileResponse fileResponse) {
    return MovieFile.builder()
        .movieFileType(fileResponse.getTargetType())
        .fileId(fileResponse.getId())
        .movie(movie)
        .build();
  }
}
