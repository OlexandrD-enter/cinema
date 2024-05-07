package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.genre.GenreAdminBriefResponse;
import com.project.cinemaservice.domain.dto.genre.GenreAdminResponse;
import com.project.cinemaservice.domain.dto.genre.GenreDataRequest;
import com.project.cinemaservice.domain.mapper.GenreMapper;
import com.project.cinemaservice.persistence.model.Genre;
import com.project.cinemaservice.persistence.repository.GenreRepository;
import com.project.cinemaservice.service.exception.EntityAlreadyExistsException;
import com.project.cinemaservice.service.impl.GenreServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {

  @Mock
  private GenreRepository genreRepository;

  @Mock
  private GenreMapper genreMapper;

  @InjectMocks
  private GenreServiceImpl genreService;

  @Test
  void createGenre_Success() {
    // Given
    GenreDataRequest request = new GenreDataRequest("Action");
    Genre genre = Genre.builder()
        .name(request.getName())
        .build();
    GenreAdminResponse expectedResponse = GenreAdminResponse.builder()
        .id(1L)
        .name(request.getName())
        .build();

    when(genreRepository.findByName("Action")).thenReturn(Optional.empty());
    when(genreMapper.toGenreEntity(request)).thenReturn(genre);
    when(genreRepository.save(genre)).thenReturn(genre);
    when(genreMapper.toGenreAdminResponse(genre)).thenReturn(expectedResponse);

    // When
    GenreAdminResponse response = genreService.createGenre(request);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void createGenre_WhenGenreAlreadyExists_ThrowsEntityAlreadyExistsException() {
    // Given
    GenreDataRequest request = new GenreDataRequest("Action");
    Genre existingGenre = Genre.builder()
        .name(request.getName())
        .build();

    when(genreRepository.findByName("Action")).thenReturn(Optional.of(existingGenre));

    // When & Then
    assertThrows(EntityAlreadyExistsException.class, () -> genreService.createGenre(request));
  }

  @Test
  void editGenre_Success() {
    // Given
    long genreId = 1L;
    GenreDataRequest request = new GenreDataRequest("Comedy");
    Genre genre = Genre.builder()
        .id(genreId)
        .name(request.getName())
        .build();
    Genre updatedGenre = Genre.builder()
        .id(genreId)
        .name("Comedy")
        .build();

    GenreAdminResponse expectedResponse = GenreAdminResponse.builder()
        .id(genreId)
        .name(updatedGenre.getName())
        .build();

    when(genreRepository.findByName("Comedy")).thenReturn(Optional.empty());
    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreRepository.save(genre)).thenReturn(updatedGenre);
    when(genreMapper.toGenreAdminResponse(updatedGenre)).thenReturn(expectedResponse);

    // When
    GenreAdminResponse response = genreService.editGenre(genreId, request);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void editGenre_WhenGenreNotFound_ThrowsEntityNotFoundException() {
    // Given
    long genreId = 1L;
    GenreDataRequest request = new GenreDataRequest("Comedy");

    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> genreService.editGenre(genreId, request));
  }

  @Test
  void deleteGenreById_Success() {
    // Given
    long genreId = 1L;
    Genre genre = Genre.builder()
        .id(genreId)
        .name("Action")
        .build();

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

    // When
    genreService.deleteGenreById(genreId);

    // Then
    verify(genreRepository, times(1)).delete(genre);
  }

  @Test
  void deleteGenreById_WhenGenreNotFound_ThrowsEntityNotFoundException() {
    // Given
    long genreId = 1L;

    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> genreService.deleteGenreById(genreId));
  }

  @Test
  void getGenreById_Success() {
    // Given
    long genreId = 1L;
    Genre genre = Genre.builder()
        .id(genreId)
        .name("Action")
        .build();
    GenreAdminResponse expectedResponse = GenreAdminResponse.builder()
        .id(genreId)
        .name(genre.getName())
        .build();

    when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
    when(genreMapper.toGenreAdminResponse(genre)).thenReturn(expectedResponse);

    // When
    GenreAdminResponse response = genreService.getGenreById(genreId);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void getGenreById_WhenGenreNotFound_ThrowsEntityNotFoundException() {
    // Given
    long genreId = 1L;

    when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> genreService.getGenreById(genreId));
  }

  @Test
  void getGenres_Success() {
    // Given
    Genre genre1 = Genre.builder()
        .id(1L)
        .name("Action")
        .build();
    Genre genre2 = Genre.builder()
        .id(2L)
        .name("Comedy")
        .build();
    List<Genre> genres = List.of(genre1, genre2);
    List<GenreAdminBriefResponse> expectedResponse = List.of(
        new GenreAdminBriefResponse(1L, "Action"),
        new GenreAdminBriefResponse(2L, "Comedy"));

    when(genreRepository.findAll()).thenReturn(genres);
    when(genreMapper.toGenreAdminBriefResponse(genre1)).thenReturn(expectedResponse.get(0));
    when(genreMapper.toGenreAdminBriefResponse(genre2)).thenReturn(expectedResponse.get(1));

    // When
    List<GenreAdminBriefResponse> response = genreService.getGenres();

    // Then
    assertEquals(expectedResponse, response);
  }
}
