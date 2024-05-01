package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieDataRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponse;
import com.project.cinemaservice.domain.mapper.MovieMapper;
import com.project.cinemaservice.persistence.enums.MovieFileType;
import com.project.cinemaservice.persistence.model.Genre;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.MovieFile;
import com.project.cinemaservice.persistence.model.MovieGenre;
import com.project.cinemaservice.persistence.repository.GenreRepository;
import com.project.cinemaservice.persistence.repository.MovieFileRepository;
import com.project.cinemaservice.persistence.repository.MovieGenreRepository;
import com.project.cinemaservice.persistence.repository.MovieRepository;
import com.project.cinemaservice.service.MediaServiceClient;
import com.project.cinemaservice.service.MovieService;
import com.project.cinemaservice.service.exception.MovieAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
  private final MovieGenreRepository movieGenreRepository;
  private final MovieFileRepository movieFileRepository;
  private final MediaServiceClient mediaServiceClient;
  private final MovieMapper movieMapper;

  @Transactional
  @Override
  public MovieAdminResponse createMovie(MovieDataRequest movieDataRequest) {
    String movieName = movieDataRequest.getName();

    log.debug("Creating movie with name {}", movieName);

    checkIfMovieExistByName(movieName);

    Movie movie = createMovieEntity(movieDataRequest);

    saveMovieGenres(movie, movieDataRequest.getMovieGenreIds());
    saveMovieFiles(movie, movieDataRequest);

    log.debug("Movie created successfully with name {}", movieName);

    return movieMapper.toMovieAdminResponse(movie);
  }

  @Transactional
  @Override
  public MovieAdminResponse editMovie(Long movieId, MovieDataRequest movieDataRequest) {
    String movieName = movieDataRequest.getName();

    log.debug("Updating movie with id {}", movieId);

    checkIfMovieExistByNameForEdit(movieName, movieId);
    Movie movie = getMovieEntityById(movieId);

    movieMapper.updateEntity(movie, movieDataRequest);

    updateMovieGenres(movie, movieDataRequest.getMovieGenreIds());

    if (movieDataRequest.getPreviewImage() != null) {
      updateMovieImageByType(movie, MovieFileType.MOVIE_PREVIEW,
          movieDataRequest.getPreviewImage());
    }
    if (movieDataRequest.getTrailerVideo() != null) {
      updateMovieImageByType(movie, MovieFileType.MOVIE_TRAILER,
          movieDataRequest.getTrailerVideo());
    }

    Movie updatedMovie = movieRepository.save(movie);

    log.debug("Movie updated successfully with id {}", movieId);

    return movieMapper.toMovieAdminResponse(updatedMovie);
  }

  private Movie getMovieEntityById(Long movieId) {
    return movieRepository.findById(movieId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Movie with id=%d not found", movieId)));
  }

  private void checkIfMovieExistByName(String movieName) {
    if (movieRepository.findByName(movieName).isPresent()) {
      throw new MovieAlreadyExistsException(
          String.format("Movie with name='%s' already exists", movieName));
    }
  }

  private void checkIfMovieExistByNameForEdit(String movieName, Long movieId) {
    if (movieRepository.existsByNameAndIdNot(movieName, movieId)) {
      throw new MovieAlreadyExistsException(
          String.format("Movie with name='%s' already exists", movieName));
    }
  }

  private Movie createMovieEntity(MovieDataRequest movieDataRequest) {
    Movie movie = movieMapper.toMovieEntity(movieDataRequest);
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

  private void saveMovieFiles(Movie movie, MovieDataRequest movieDataRequest) {
    MovieFileRequest previewFile = createMovieFileRequest(movie.getId(),
        MovieFileType.MOVIE_PREVIEW,
        movieDataRequest.getPreviewImage());

    MovieFileResponse previewFileResponse = mediaServiceClient.uploadFile(previewFile,
        movieDataRequest.getPreviewImage());

    MovieFileRequest trailerFile = createMovieFileRequest(movie.getId(),
        MovieFileType.MOVIE_TRAILER,
        movieDataRequest.getTrailerVideo());

    MovieFileResponse trailerFileResponse = mediaServiceClient.uploadFile(trailerFile,
        movieDataRequest.getTrailerVideo());

    MovieFile movieFilePreview = createMovieFile(movie, previewFileResponse);
    MovieFile movieFileTrailer = createMovieFile(movie, trailerFileResponse);

    movie.setMovieFiles(List.of(movieFilePreview, movieFileTrailer));
  }

  private MovieFile updateMovieImageByType(Movie movie, MovieFileType movieFileType,
      MultipartFile previewImage) {

    MovieFile moviePreviewFile = movieFileRepository.findByMovieAndMovieFileType(movie,
        movieFileType).orElseThrow(() -> new EntityNotFoundException(
        "Movie PreviewFile not found for movie with id {} " + movie.getId())
    );

    mediaServiceClient.deleteFileById(moviePreviewFile.getFileId());

    MovieFileRequest previewFile = createMovieFileRequest(movie.getId(), movieFileType,
        previewImage);

    MovieFileResponse previewFileResponse = mediaServiceClient.uploadFile(previewFile,
        previewImage);

    moviePreviewFile.setFileId(previewFileResponse.getId());

    return createMovieFile(movie, previewFileResponse);
  }

  private void updateMovieGenres(Movie movie, List<Long> genreIds) {
    if (genreIds != null && !genreIds.isEmpty()) {
      List<MovieGenre> existingGenres = movie.getMovieGenres();
      List<Long> existingGenreIds = existingGenres.stream()
          .map(MovieGenre::getGenre)
          .map(Genre::getId)
          .toList();

      deleteOldMovieGenres(movie, existingGenreIds, genreIds);

      for (Long genreId : genreIds) {
        if (!existingGenreIds.contains(genreId)) {
          Genre genre = genreRepository.findById(genreId)
              .orElseThrow(
                  () -> new EntityNotFoundException("Genre not found with id: " + genreId));
          existingGenres.add(MovieGenre.builder()
              .genre(genre)
              .movie(movie)
              .build());
        }
      }
    }
  }

  private void deleteOldMovieGenres(Movie movie, List<Long> existingIds, List<Long> newIds) {
    Set<Long> set = new HashSet<>(newIds);
    List<Long> deletedIds = new ArrayList<>();

    for (Long id : existingIds) {
      if (!set.contains(id)) {
        deletedIds.add(id);
        movie.removeGenreEntityFromEntitiesListByGenreId(id);
      }
    }
    movieGenreRepository.deleteAllByGenreIds(deletedIds);
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
