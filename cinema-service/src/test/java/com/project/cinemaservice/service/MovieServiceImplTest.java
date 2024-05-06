package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieClientResponse;
import com.project.cinemaservice.domain.dto.movie.MovieDataRequest;
import com.project.cinemaservice.domain.dto.movie.MovieEditRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponse;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponseUrl;
import com.project.cinemaservice.domain.dto.movie.MovieFiltersRequest;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetails;
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
import com.project.cinemaservice.service.exception.AgeViolationException;
import com.project.cinemaservice.service.exception.MovieAlreadyExistsException;
import com.project.cinemaservice.service.impl.MovieServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

  @Mock
  private MovieRepository movieRepository;

  @Mock
  private GenreRepository genreRepository;

  @Mock
  private MovieGenreRepository movieGenreRepository;

  @Mock
  private MovieFileRepository movieFileRepository;

  @Mock
  private MediaServiceClient mediaServiceClient;

  @Mock
  private MovieMapper movieMapper;

  @InjectMocks
  private MovieServiceImpl movieService;

  @Test
  void createMovie_Success() {
    // Given
    MockMultipartFile previewImage = new MockMultipartFile("previewImage", "previewImage.jpg",
        "image/jpeg", "previewImage".getBytes());
    MockMultipartFile trailerVideo = new MockMultipartFile("trailerVideo", "trailerVideo.mp4",
        "video/mp4", "trailerVideo".getBytes());

    MovieDataRequest movieDataRequest = new MovieDataRequest();
    movieDataRequest.setName("Avengers");
    movieDataRequest.setMovieGenreIds(List.of(1L, 2L));
    movieDataRequest.setPreviewImage(previewImage);
    movieDataRequest.setTrailerVideo(trailerVideo);

    Movie movieEntity = new Movie();
    movieEntity.setId(1L);

    MovieFileResponse uploadedPreviewFile = new MovieFileResponse();
    uploadedPreviewFile.setId(101L);
    MovieFileResponse uploadedTrailerFile = new MovieFileResponse();
    uploadedTrailerFile.setId(102L);

    MovieFileResponseUrl previewResponseUrl = new MovieFileResponseUrl("previewAccessUrl");
    MovieFileResponseUrl trailerResponseUrl = new MovieFileResponseUrl("trailerAccessUrl");

    MovieAdminResponse movieAdminResponse = new MovieAdminResponse();
    movieAdminResponse.setId(1L);
    movieAdminResponse.setName("Avengers");
    movieAdminResponse.setPreviewUrl(previewResponseUrl.getAccessUrl());
    movieAdminResponse.setTrailerUrl(trailerResponseUrl.getAccessUrl());

    when(movieMapper.toMovieEntity(movieDataRequest)).thenReturn(movieEntity);
    when(movieMapper.toMovieAdminResponse(movieEntity, previewResponseUrl.getAccessUrl(),
        trailerResponseUrl.getAccessUrl())).thenReturn(movieAdminResponse);
    when(movieRepository.save(movieEntity)).thenReturn(movieEntity);
    when(mediaServiceClient.uploadFile(any(), eq(previewImage))).thenReturn(uploadedPreviewFile);
    when(mediaServiceClient.uploadFile(any(), eq(trailerVideo))).thenReturn(uploadedTrailerFile);
    when(mediaServiceClient.getFile(101L)).thenReturn(previewResponseUrl);
    when(mediaServiceClient.getFile(102L)).thenReturn(trailerResponseUrl);

    // When
    MovieAdminResponse adminResponse = movieService.createMovie(movieDataRequest);

    // Then
    assertEquals("Avengers", adminResponse.getName());
    assertNotNull(adminResponse.getPreviewUrl());
    assertNotNull(adminResponse.getTrailerUrl());
  }

  @Test
  void createMovie_WhenMovieAlreadyExists_ThrowsMovieAlreadyExistsException() {
    // Given
    MovieDataRequest movieDataRequest = new MovieDataRequest();
    movieDataRequest.setName("Avengers");
    movieDataRequest.setMovieGenreIds(List.of(1L, 2L));

    when(movieRepository.findByName("Avengers")).thenReturn(Optional.of(new Movie()));

    // When & Then
    assertThrows(MovieAlreadyExistsException.class,
        () -> movieService.createMovie(movieDataRequest));
  }

  @Test
  void editMovie_Success() {
    // Given
    MockMultipartFile previewImage = new MockMultipartFile("previewImage", "previewImage.jpg",
        "image/jpeg", "previewImage".getBytes());
    MockMultipartFile trailerVideo = new MockMultipartFile("trailerVideo", "trailerVideo.mp4",
        "video/mp4", "trailerVideo".getBytes());
    Long movieId = 1L;
    MovieEditRequest movieEditRequest = new MovieEditRequest();
    movieEditRequest.setName("New Movie Name");
    movieEditRequest.setMovieGenreIds(List.of(1L, 2L));
    movieEditRequest.setPreviewImage(previewImage);
    movieEditRequest.setTrailerVideo(trailerVideo);

    Movie existingMovie = new Movie();
    existingMovie.setId(movieId);
    existingMovie.setName("Old Movie Name");
    Genre genre1 = new Genre();
    genre1.setId(1L);
    genre1.setName("Action");
    Genre genre2 = new Genre();
    genre2.setId(2L);
    genre2.setName("Adventure");
    existingMovie.setMovieGenres(List.of(
        MovieGenre.builder()
            .genre(genre1)
            .movie(existingMovie)
            .build(),
        MovieGenre.builder()
            .genre(genre2)
            .movie(existingMovie)
            .build())
    );

    MovieFile mockPreviewMovieFile = new MovieFile();
    mockPreviewMovieFile.setFileId(101L);
    mockPreviewMovieFile.setMovieFileType(MovieFileType.MOVIE_PREVIEW);
    MovieFile mockTrailerMovieFile = new MovieFile();
    mockTrailerMovieFile.setFileId(102L);
    mockTrailerMovieFile.setMovieFileType(MovieFileType.MOVIE_TRAILER);

    existingMovie.setMovieFiles(List.of(mockPreviewMovieFile, mockTrailerMovieFile));

    MovieFileResponse uploadedPreviewFile = new MovieFileResponse();
    uploadedPreviewFile.setId(101L);
    MovieFileResponse uploadedTrailerFile = new MovieFileResponse();
    uploadedTrailerFile.setId(102L);

    MovieFileResponseUrl previewResponseUrl = new MovieFileResponseUrl("previewAccessUrl");
    MovieFileResponseUrl trailerResponseUrl = new MovieFileResponseUrl("trailerAccessUrl");

    MovieAdminResponse movieAdminResponse = new MovieAdminResponse();
    movieAdminResponse.setId(1L);
    movieAdminResponse.setName("New Movie Name");
    movieAdminResponse.setPreviewUrl(previewResponseUrl.getAccessUrl());
    movieAdminResponse.setTrailerUrl(trailerResponseUrl.getAccessUrl());

    when(movieRepository.save(any(Movie.class))).thenReturn(existingMovie);
    when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
    when(movieFileRepository.findByMovieAndMovieFileType(existingMovie,
        mockPreviewMovieFile.getMovieFileType())).thenReturn(
        Optional.of(mockPreviewMovieFile));
    when(movieFileRepository.findByMovieAndMovieFileType(existingMovie,
        mockTrailerMovieFile.getMovieFileType())).thenReturn(
        Optional.of(mockTrailerMovieFile));
    when(mediaServiceClient.uploadFile(any(), eq(previewImage))).thenReturn(uploadedPreviewFile);
    when(mediaServiceClient.uploadFile(any(), eq(trailerVideo))).thenReturn(uploadedTrailerFile);
    when(mediaServiceClient.getFile(101L)).thenReturn(previewResponseUrl);
    when(mediaServiceClient.getFile(102L)).thenReturn(trailerResponseUrl);
    when(movieMapper.toMovieAdminResponse(existingMovie, previewResponseUrl.getAccessUrl(),
        trailerResponseUrl.getAccessUrl())).thenReturn(movieAdminResponse);

    // When
    MovieAdminResponse adminResponse = movieService.editMovie(movieId, movieEditRequest);

    // Then
    assertEquals("New Movie Name", adminResponse.getName());
    assertNotNull(adminResponse.getPreviewUrl());
    assertNotNull(adminResponse.getTrailerUrl());
  }

  @Test
  void editMovie_WhenMovieAlreadyExists_ThrowsMovieAlreadyExistsException() {
    // Given
    Long movieId = 1L;
    MovieEditRequest movieEditRequest = new MovieEditRequest();
    movieEditRequest.setName("Existing Movie Name");
    movieEditRequest.setMovieGenreIds(List.of(1L, 2L));

    when(movieRepository.existsByNameAndIdNot("Existing Movie Name", movieId)).thenReturn(true);

    // When & Then
    assertThrows(MovieAlreadyExistsException.class,
        () -> movieService.editMovie(movieId, movieEditRequest));
  }

  @Test
  void editMovie_WhenInvalidGenreIds_ThrowsEntityNotFoundException() {
    // Given
    Long movieId = 1L;
    MovieEditRequest movieEditRequest = new MovieEditRequest();
    movieEditRequest.setName("Existing Movie Name");
    movieEditRequest.setMovieGenreIds(List.of(3L));

    Movie existingMovie = new Movie();
    existingMovie.setId(movieId);
    existingMovie.setName("Old Movie Name");
    Genre genre1 = new Genre();
    genre1.setId(1L);
    genre1.setName("Action");
    Genre genre2 = new Genre();
    genre2.setId(2L);
    genre2.setName("Adventure");
    existingMovie.setMovieGenres(new ArrayList<>(List.of(
        MovieGenre.builder()
            .genre(genre1)
            .movie(existingMovie)
            .build(),
        MovieGenre.builder()
            .genre(genre2)
            .movie(existingMovie)
            .build()))
    );

    when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
    when(genreRepository.findById((3L))).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> movieService.editMovie(movieId, movieEditRequest));
  }

  @Test
  void editMovie_WhenNotExistMovieId_ThrowsEntityNotFoundException() {
    // Given
    Long movieId = 1L;
    MovieEditRequest movieEditRequest = new MovieEditRequest();
    movieEditRequest.setName("Existing Movie Name");
    movieEditRequest.setMovieGenreIds(List.of(1L, 2L));

    when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> movieService.editMovie(movieId, movieEditRequest));
  }

  @Test
  void getMovieForAdminById_Success() {
    // Given
    Long movieId = 1L;
    Movie existingMovie = new Movie();
    existingMovie.setId(movieId);
    existingMovie.setName("Existing Movie");

    MovieFile previewMovieFile = new MovieFile();
    previewMovieFile.setFileId(101L);
    previewMovieFile.setMovieFileType(MovieFileType.MOVIE_PREVIEW);
    MovieFile trailerMovieFile = new MovieFile();
    trailerMovieFile.setFileId(102L);
    trailerMovieFile.setMovieFileType(MovieFileType.MOVIE_TRAILER);
    existingMovie.setMovieFiles(List.of(previewMovieFile, trailerMovieFile));

    MovieFileResponseUrl previewResponseUrl = new MovieFileResponseUrl("previewAccessUrl");
    MovieFileResponseUrl trailerResponseUrl = new MovieFileResponseUrl("trailerAccessUrl");

    MovieAdminResponse movieAdminResponse = new MovieAdminResponse();
    movieAdminResponse.setId(1L);
    movieAdminResponse.setName("Existing Movie");
    movieAdminResponse.setPreviewUrl(previewResponseUrl.getAccessUrl());
    movieAdminResponse.setTrailerUrl(trailerResponseUrl.getAccessUrl());

    when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
    when(mediaServiceClient.getFile(101L)).thenReturn(previewResponseUrl);
    when(mediaServiceClient.getFile(102L)).thenReturn(trailerResponseUrl);
    when(movieMapper.toMovieAdminResponse(existingMovie, previewResponseUrl.getAccessUrl(),
        trailerResponseUrl.getAccessUrl()))
        .thenReturn(movieAdminResponse);

    // When
    MovieAdminResponse adminResponse = movieService.getMovieForAdminById(movieId);

    // Then
    assertEquals(existingMovie.getId(), adminResponse.getId());
    assertEquals(existingMovie.getName(), adminResponse.getName());
    assertEquals("previewAccessUrl", adminResponse.getPreviewUrl());
    assertEquals("trailerAccessUrl", adminResponse.getTrailerUrl());
  }

  @Test
  void getMovieForAdminById_WhenNotExist_ThrowsEntityNotFoundException() {
    // Given
    Long movieId = 1L;
    when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> movieService.getMovieForAdminById(movieId));
  }

  @Test
  void getMovieForClientById_Success() {
    // Given
    Long movieId = 1L;
    Movie existingMovie = new Movie();
    existingMovie.setId(movieId);
    existingMovie.setName("Existing Movie");

    MovieFile previewMovieFile = new MovieFile();
    previewMovieFile.setFileId(101L);
    previewMovieFile.setMovieFileType(MovieFileType.MOVIE_PREVIEW);
    MovieFile trailerMovieFile = new MovieFile();
    trailerMovieFile.setFileId(102L);
    trailerMovieFile.setMovieFileType(MovieFileType.MOVIE_TRAILER);
    existingMovie.setMovieFiles(List.of(previewMovieFile, trailerMovieFile));

    MovieFileResponseUrl previewResponseUrl = new MovieFileResponseUrl("previewAccessUrl");
    MovieFileResponseUrl trailerResponseUrl = new MovieFileResponseUrl("trailerAccessUrl");

    MovieClientResponse movieClientResponse = new MovieAdminResponse();
    movieClientResponse.setId(1L);
    movieClientResponse.setName("Existing Movie");
    movieClientResponse.setPreviewUrl(previewResponseUrl.getAccessUrl());
    movieClientResponse.setTrailerUrl(trailerResponseUrl.getAccessUrl());

    when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
    when(mediaServiceClient.getFile(101L)).thenReturn(previewResponseUrl);
    when(mediaServiceClient.getFile(102L)).thenReturn(trailerResponseUrl);
    when(movieMapper.toMovieClientResponse(existingMovie, previewResponseUrl.getAccessUrl(),
        trailerResponseUrl.getAccessUrl()))
        .thenReturn(movieClientResponse);

    // When
    MovieClientResponse clientResponse = movieService.getMovieForClientById(movieId);

    // Then
    assertEquals(existingMovie.getId(), clientResponse.getId());
    assertEquals(existingMovie.getName(), clientResponse.getName());
    assertEquals("previewAccessUrl", clientResponse.getPreviewUrl());
    assertEquals("trailerAccessUrl", clientResponse.getTrailerUrl());
  }

  @Test
  void getMovieForClientById_WhenNotExist_ThrowsEntityNotFoundException() {
    // Given
    Long movieId = 1L;
    when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> movieService.getMovieForClientById(movieId));
  }

  @Test
  void deleteMovieById_Success() {
    // Given
    Long movieId = 1L;
    Movie existingMovie = new Movie();
    existingMovie.setId(movieId);
    existingMovie.setName("Existing Movie");

    MovieFile previewMovieFile = new MovieFile();
    previewMovieFile.setFileId(101L);
    MovieFile trailerMovieFile = new MovieFile();
    trailerMovieFile.setFileId(102L);
    existingMovie.setMovieFiles(List.of(previewMovieFile, trailerMovieFile));

    when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));

    // When
    movieService.deleteMovieById(movieId);

    // Then
    verify(movieRepository, times(1)).delete(existingMovie);
    verify(mediaServiceClient, times(1)).deleteFileById(previewMovieFile.getFileId());
    verify(mediaServiceClient, times(1)).deleteFileById(trailerMovieFile.getFileId());
  }

  @Test
  void deleteMovieById_WhenNotExist_ThrowsEntityNotFoundException() {
    // Given
    Long movieId = 1L;

    when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> movieService.deleteMovieById(movieId));
  }

  @Test
  void getAllMoviesByFilter_Success() {
    // Given
    Pageable pageable = Pageable.unpaged();
    MovieFiltersRequest movieFiltersRequest = new MovieFiltersRequest();
    movieFiltersRequest.setGenreIds(List.of(1L, 2L));
    Genre genre1 = new Genre();
    genre1.setId(1L);
    genre1.setName("Action");
    Genre genre2 = new Genre();
    genre2.setId(2L);
    genre2.setName("Adventure");

    List<Genre> genres = new ArrayList<>(List.of(genre1, genre2));

    Page<MoviePageDetails> moviePage = new PageImpl<>(List.of(MoviePageDetails.builder()
        .fileId(1L)
        .build()));
    Page<MoviePageDetailsResponse> expectedMoviePage = new PageImpl<>(
        List.of(MoviePageDetailsResponse.builder().build()));

    MovieFileResponseUrl previewResponseUrl = new MovieFileResponseUrl("previewAccessUrl");

    when(genreRepository.findAllById(movieFiltersRequest.getGenreIds())).thenReturn(genres);
    when(movieRepository.findAllByFilters(any(), any())).thenReturn(moviePage);
    when(movieMapper.toMoviePageDetailsResponse(any(), anyString())).thenReturn(
        new MoviePageDetailsResponse());
    when(mediaServiceClient.getFile(1L)).thenReturn(previewResponseUrl);

    // When
    Page<MoviePageDetailsResponse> result = movieService.getAllMoviesByFilter(pageable,
        movieFiltersRequest);

    // Then
    assertEquals(expectedMoviePage.getContent().size(), result.getContent().size());
    verify(movieRepository, times(1)).findAllByFilters(any(), any());
    verify(movieMapper, times(moviePage.getContent().size()))
        .toMoviePageDetailsResponse(any(), anyString());
  }


  @Test
  void getAllMoviesByFilter_WhenGenresNotFound_ThrowsEntityNotFoundException() {
    // Given
    Pageable pageable = Pageable.unpaged();
    MovieFiltersRequest movieFiltersRequest = new MovieFiltersRequest();
    movieFiltersRequest.setGenreIds(List.of(1L, 2L));
    when(genreRepository.findAllById(movieFiltersRequest.getGenreIds())).thenReturn(
        new ArrayList<>());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> movieService.getAllMoviesByFilter(pageable,
            movieFiltersRequest));
  }

  @Test
  void getAllMoviesByFilter_WhenFromAgeGreaterThanToAge_ThrowsAgeViolationException() {
    // Given
    Pageable pageable = Pageable.unpaged();
    MovieFiltersRequest movieFiltersRequest = new MovieFiltersRequest();
    movieFiltersRequest.setMinAge(30);
    movieFiltersRequest.setMaxAge(20);
    movieFiltersRequest.setGenreIds(List.of(1L, 2L));

    Genre genre1 = new Genre();
    genre1.setId(1L);
    genre1.setName("Action");
    Genre genre2 = new Genre();
    genre2.setId(2L);
    genre2.setName("Adventure");

    List<Genre> genres = new ArrayList<>(List.of(genre1, genre2));

    when(genreRepository.findAllById(movieFiltersRequest.getGenreIds())).thenReturn(genres);

    // When & Then
    assertThrows(AgeViolationException.class, () -> movieService.getAllMoviesByFilter(pageable,
        movieFiltersRequest));
  }

  @Test
  void getAllMoviesByFilter_WhenFromAgeOrToAgeIsNull_ThrowsAgeViolationException() {
    // Given
    Pageable pageable = Pageable.unpaged();
    MovieFiltersRequest movieFiltersRequest1 = new MovieFiltersRequest();
    movieFiltersRequest1.setMinAge(null);
    movieFiltersRequest1.setMaxAge(20);
    movieFiltersRequest1.setGenreIds(List.of(1L, 2L));

    MovieFiltersRequest movieFiltersRequest2 = new MovieFiltersRequest();
    movieFiltersRequest2.setMinAge(20);
    movieFiltersRequest2.setMaxAge(null);
    movieFiltersRequest2.setGenreIds(List.of(1L, 2L));

    Genre genre1 = new Genre();
    genre1.setId(1L);
    genre1.setName("Action");
    Genre genre2 = new Genre();
    genre2.setId(2L);
    genre2.setName("Adventure");

    List<Genre> genres = new ArrayList<>(List.of(genre1, genre2));

    when(genreRepository.findAllById(any())).thenReturn(genres);

    // When & Then
    assertAll(
        () -> assertThrows(AgeViolationException.class,
            () -> movieService.getAllMoviesByFilter(pageable, movieFiltersRequest1)),
        () -> assertThrows(AgeViolationException.class,
            () -> movieService.getAllMoviesByFilter(pageable, movieFiltersRequest2))
    );
  }
}
