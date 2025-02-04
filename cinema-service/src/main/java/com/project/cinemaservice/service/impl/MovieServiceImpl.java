package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieBriefInfo;
import com.project.cinemaservice.domain.dto.movie.MovieClientResponse;
import com.project.cinemaservice.domain.dto.movie.MovieDataRequest;
import com.project.cinemaservice.domain.dto.movie.MovieEditRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponse;
import com.project.cinemaservice.domain.dto.movie.MovieFilters;
import com.project.cinemaservice.domain.dto.movie.MovieFiltersRequest;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetails;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsResponse;
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
import com.project.cinemaservice.service.exception.AgeViolationException;
import com.project.cinemaservice.service.exception.EntityAlreadyExistsException;
import com.project.cinemaservice.service.exception.MoviePublishException;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    movie.setIsPublish(false);

    saveMovieGenres(movie, movieDataRequest.getMovieGenreIds());

    MovieFile previewMovieFile = saveMovieFile(movie, MovieFileType.MOVIE_PREVIEW,
        movieDataRequest.getPreviewImage());
    MovieFile trailerMovieFile = saveMovieFile(movie, MovieFileType.MOVIE_TRAILER,
        movieDataRequest.getTrailerVideo());

    movie.setMovieFiles(List.of(previewMovieFile, trailerMovieFile));

    String previewFileUrl = getFileAccessUrl(previewMovieFile.getFileId());
    String trailerFileUrl = getFileAccessUrl(trailerMovieFile.getFileId());

    log.debug("Movie created successfully with name {}", movieName);

    return movieMapper.toMovieAdminResponse(movie, previewFileUrl, trailerFileUrl);
  }

  @Transactional
  @Override
  public MovieAdminResponse editMovie(Long movieId, MovieEditRequest movieEditRequest) {
    String movieName = movieEditRequest.getName();

    log.debug("Updating movie with id {}", movieId);

    checkIfMovieExistByNameForEdit(movieName, movieId);
    Movie movie = getMovieEntityById(movieId);

    movieMapper.updateEntity(movie, movieEditRequest);

    updateMovieGenres(movie, movieEditRequest.getMovieGenreIds());

    if (movieEditRequest.getPreviewImage() != null) {
      updateMovieFileByType(movie, MovieFileType.MOVIE_PREVIEW,
          movieEditRequest.getPreviewImage());
    }

    if (movieEditRequest.getTrailerVideo() != null) {
      updateMovieFileByType(movie, MovieFileType.MOVIE_TRAILER,
          movieEditRequest.getTrailerVideo());
    }

    Movie updatedMovie = movieRepository.save(movie);

    String previewFileUrl = getFileUrlByType(movie, MovieFileType.MOVIE_PREVIEW);
    String trailerFileUrl = getFileUrlByType(movie, MovieFileType.MOVIE_TRAILER);

    log.debug("Movie updated successfully with id {}", movieId);

    return movieMapper.toMovieAdminResponse(updatedMovie, previewFileUrl, trailerFileUrl);
  }

  @Transactional
  @Override
  public MovieAdminResponse getMovieForAdminById(Long movieId) {
    Movie movie = getMovieEntityById(movieId);

    String previewFileUrl = getFileUrlByType(movie, MovieFileType.MOVIE_PREVIEW);
    String trailerFileUrl = getFileUrlByType(movie, MovieFileType.MOVIE_TRAILER);

    return movieMapper.toMovieAdminResponse(movie, previewFileUrl, trailerFileUrl);
  }

  @Transactional
  @Override
  public MovieClientResponse getMovieForClientById(Long movieId) {
    Movie movie = getMovieEntityById(movieId);

    if (!movie.getIsPublish()) {
      throw new EntityNotFoundException(String.format("Movie with id=%d not found", movieId));
    }

    String previewFileUrl = getFileUrlByType(movie, MovieFileType.MOVIE_PREVIEW);
    String trailerFileUrl = getFileUrlByType(movie, MovieFileType.MOVIE_TRAILER);

    return movieMapper.toMovieClientResponse(movie, previewFileUrl, trailerFileUrl);
  }

  @Transactional
  @Override
  public void deleteMovieById(Long movieId) {
    log.debug("Deleting movie with id {}", movieId);

    Movie movie = getMovieEntityById(movieId);

    if (movie.getIsPublish()) {
      throw new MoviePublishException(String.format("Movie with id=%d published", movieId));
    }

    movieRepository.delete(movie);

    movie.getMovieFiles().forEach(movieFile ->
        mediaServiceClient.deleteFileById(movieFile.getFileId()));

    log.debug("Deleted movie with id {}", movieId);
  }

  @Transactional
  @Override
  public Page<MoviePageDetailsResponse> getAllMoviesByFiltersForClient(Pageable pageable,
      MovieFiltersRequest movieFiltersRequest) {
    List<Genre> genres = retrieveGenresFromFilterRequest(movieFiltersRequest);

    validateAgeRangeFromFilterRequest(movieFiltersRequest);
    MovieFilters movieFilters = mapFilterRequestToFilters(movieFiltersRequest, genres);
    movieFilters.setIsPublish(true);

    Page<MoviePageDetails> moviePage = movieRepository.findAllByFilters(pageable, movieFilters);

    List<MoviePageDetailsResponse> moviePageDetailsList = moviePage.stream()
        .map(movie -> {
          String previewUrl = getFileAccessUrl(movie.getFileId());
          return movieMapper.toMoviePageDetailsResponse(movie, previewUrl);
        }).toList();

    return new PageImpl<>(moviePageDetailsList, pageable, moviePage.getTotalElements());
  }

  @Transactional
  @Override
  public Page<MoviePageDetailsAdminResponse> getAllMoviesByFiltersForAdmin(Pageable pageable,
      MovieFiltersRequest movieFiltersRequest) {
    List<Genre> genres = retrieveGenresFromFilterRequest(movieFiltersRequest);

    validateAgeRangeFromFilterRequest(movieFiltersRequest);
    MovieFilters movieFilters = mapFilterRequestToFilters(movieFiltersRequest, genres);

    Page<MoviePageDetails> moviePage = movieRepository.findAllByFilters(pageable, movieFilters);

    List<MoviePageDetailsAdminResponse> moviePageDetailsList = moviePage.stream()
        .map(movie -> {
          String previewUrl = getFileAccessUrl(movie.getFileId());
          return movieMapper.toMoviePageDetailsAdminResponse(movie, previewUrl);
        }).toList();

    return new PageImpl<>(moviePageDetailsList, pageable, moviePage.getTotalElements());
  }

  @Transactional
  @Override
  public MovieBriefInfo changeMovieStatus(Long movieId, boolean movieStatus) {
    log.debug("Changing movie status with id {}", movieId);

    Movie movie = getMovieEntityById(movieId);
    movie.setIsPublish(movieStatus);

    Movie savedMovie = movieRepository.save(movie);

    log.debug("Changed movie status with id {} to {}", movieId, movieStatus);

    return movieMapper.toMovieBriefInfo(savedMovie);
  }

  private MovieFilters mapFilterRequestToFilters(MovieFiltersRequest movieFiltersRequest,
      List<Genre> genres) {
    return movieMapper.toMovieFilters(movieFiltersRequest, genres);
  }

  private Movie getMovieEntityById(Long movieId) {
    return movieRepository.findById(movieId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Movie with id=%d not found", movieId)));
  }

  private void checkIfMovieExistByName(String movieName) {
    if (movieRepository.findByName(movieName).isPresent()) {
      throw new EntityAlreadyExistsException(
          String.format("Movie with name='%s' already exists", movieName));
    }
  }

  private void checkIfMovieExistByNameForEdit(String movieName, Long movieId) {
    if (movieRepository.existsByNameAndIdNot(movieName, movieId)) {
      throw new EntityAlreadyExistsException(
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

  private MovieFile saveMovieFile(Movie movie, MovieFileType movieFileType, MultipartFile file) {
    MovieFileRequest previewFile = createMovieFileRequest(movie.getId(),
        movieFileType,
        file);

    MovieFileResponse movieFileResponse = mediaServiceClient.uploadFile(previewFile,
        file);

    return createMovieFile(movie, movieFileResponse);
  }

  private MovieFile updateMovieFileByType(Movie movie, MovieFileType movieFileType,
      MultipartFile previewImage) {

    MovieFile moviePreviewFile = movieFileRepository.findByMovieAndMovieFileType(movie,
        movieFileType).orElseThrow(() -> new EntityNotFoundException(
        String.format("Movie PreviewFile not found for movie with id=%d ", movie.getId())));

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
              .orElseThrow(() -> new EntityNotFoundException(
                  String.format("Genre not found with id=%d ", genreId)));

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

  private String getFileUrlByType(Movie movie, MovieFileType fileType) {
    for (MovieFile movieFile : movie.getMovieFiles()) {
      if (movieFile.getMovieFileType().equals(fileType)) {
        return getFileAccessUrl(movieFile.getFileId());
      }
    }
    return null;
  }

  private String getFileAccessUrl(Long fileId) {
    return mediaServiceClient.getFile(fileId).getAccessUrl();
  }

  private List<Genre> retrieveGenresFromFilterRequest(
      MovieFiltersRequest movieFiltersRequest) {
    List<Genre> genres = new ArrayList<>();
    List<Long> genreIds = movieFiltersRequest.getGenreIds();

    if (genreIds != null && !genreIds.isEmpty()) {
      genres = genreRepository.findAllById(genreIds);

      if (genres.size() != genreIds.size()) {
        List<Long> foundGenreIds = genres.stream()
            .map(Genre::getId)
            .toList();
        List<Long> notFoundGenreIds = genreIds.stream()
            .filter(id -> !foundGenreIds.contains(id))
            .toList();
        throw new EntityNotFoundException("Genres not found: " + notFoundGenreIds);
      }
    }
    return genres;
  }

  private void validateAgeRangeFromFilterRequest(
      MovieFiltersRequest movieFiltersRequest) {
    Integer fromAge = movieFiltersRequest.getMinAge();
    Integer toAge = movieFiltersRequest.getMaxAge();

    if (fromAge != null && toAge != null && fromAge.compareTo(toAge) > 0) {
      throw new AgeViolationException("'From age' should be lower than 'To age'");
    } else if ((fromAge == null && toAge != null) || (fromAge != null && toAge == null)) {
      throw new AgeViolationException("Both 'From age' and 'To age' must be provided");
    }
  }
}
