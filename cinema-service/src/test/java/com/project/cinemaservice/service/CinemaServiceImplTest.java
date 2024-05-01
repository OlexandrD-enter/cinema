package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.cinema.CinemaAdminResponse;
import com.project.cinemaservice.domain.dto.cinema.CinemaDataRequest;
import com.project.cinemaservice.domain.mapper.CinemaMapper;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.repository.CinemaRepository;
import com.project.cinemaservice.service.exception.CinemaAlreadyExistsException;
import com.project.cinemaservice.service.impl.CinemaServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CinemaServiceImplTest {

  @Mock
  private CinemaRepository cinemaRepository;
  @Mock
  private CinemaMapper cinemaMapper;

  @InjectMocks
  private CinemaServiceImpl cinemaService;

  @Test
  void createCinema_Success() {
    // Given
    CinemaDataRequest request = new CinemaDataRequest("Cinema", "City", "Address, 1");
    Cinema cinema = Cinema.builder()
        .id(1L)
        .name(request.getName())
        .city(request.getCity())
        .streetAddress(request.getStreetAddress())
        .build();
    CinemaAdminResponse expectedResponse = CinemaAdminResponse.builder()
        .id(cinema.getId())
        .name(cinema.getName())
        .city(cinema.getCity())
        .streetAddress(cinema.getStreetAddress())
        .build();

    when(cinemaRepository.findByName("Cinema")).thenReturn(Optional.empty());
    when(cinemaMapper.toCinemaEntity(request)).thenReturn(cinema);
    when(cinemaRepository.save(cinema)).thenReturn(cinema);
    when(cinemaMapper.toCinemaAdminResponse(cinema)).thenReturn(expectedResponse);

    // When
    CinemaAdminResponse response = cinemaService.createCinema(request);

    // Then
    assertEquals(expectedResponse.getId(), response.getId());
    assertEquals(expectedResponse.getName(), response.getName());
    assertEquals(expectedResponse.getCity(), response.getCity());
    assertEquals(expectedResponse.getStreetAddress(), response.getStreetAddress());
  }

  @Test
  void createCinema_WhenCinemaAlreadyExists_ThrowsCinemaAlreadyExistsException() {
    // Given
    CinemaDataRequest request = new CinemaDataRequest("Cinema", "City", "Address, 1");
    when(cinemaRepository.findByName("Cinema")).thenReturn(Optional.of(new Cinema()));

    // When & Then
    assertThrows(CinemaAlreadyExistsException.class, () -> cinemaService.createCinema(request));
  }

  @Test
  void editCinema_Success() {
    // Given
    long cinemaId = 1L;
    CinemaDataRequest request = new CinemaDataRequest("Cinema", "City", "Address, 1");
    Cinema cinema = Cinema.builder()
        .id(cinemaId)
        .name("Old cinema")
        .city(request.getCity())
        .streetAddress(request.getStreetAddress())
        .build();
    CinemaAdminResponse expectedResponse = CinemaAdminResponse.builder()
        .id(cinema.getId())
        .name(request.getName())
        .city(cinema.getCity())
        .streetAddress(cinema.getStreetAddress())
        .build();

    when(cinemaRepository.findByName("Cinema")).thenReturn(Optional.empty());
    when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.of(cinema));
    when(cinemaRepository.save(cinema)).thenReturn(cinema);
    when(cinemaMapper.toCinemaAdminResponse(cinema)).thenReturn(expectedResponse);

    // When
    CinemaAdminResponse response = cinemaService.editCinema(cinemaId, request);

    // Then
    assertEquals(expectedResponse.getId(), response.getId());
    assertEquals(expectedResponse.getName(), response.getName());
    assertEquals(expectedResponse.getCity(), response.getCity());
    assertEquals(expectedResponse.getStreetAddress(), response.getStreetAddress());
  }

  @Test
  void editCinema_WhenCinemaNotFound_ThrowsEntityNotFoundException() {
    // Given
    long cinemaId = 1L;
    CinemaDataRequest request = new CinemaDataRequest("Cinema", "City", "Address, 1");

    when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> cinemaService.editCinema(cinemaId, request));
  }

  @Test
  void deleteCinemaById_Success() {
    // Given
    long cinemaId = 1L;
    Cinema cinema = Cinema.builder()
        .id(1L)
        .name("Cinema")
        .city("City")
        .streetAddress("Address, 1")
        .build();

    when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.of(cinema));

    // When
    cinemaService.deleteCinemaById(cinemaId);

    // Then
    verify(cinemaRepository, times(1)).delete(cinema);
  }

  @Test
  void deleteCinemaById_WhenCinemaNotFound_ThrowsEntityNotFoundException() {
    // Given
    long cinemaId = 1L;

    when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> cinemaService.deleteCinemaById(cinemaId));
  }

  @Test
  void getCinemaById_Success() {
    // Given
    long cinemaId = 1L;
    Cinema cinema = Cinema.builder()
        .id(cinemaId)
        .name("Cinema")
        .city("City")
        .streetAddress("Address, 1")
        .build();

    CinemaAdminResponse expectedResponse = CinemaAdminResponse.builder()
        .id(cinema.getId())
        .name(cinema.getName())
        .city(cinema.getCity())
        .streetAddress(cinema.getStreetAddress())
        .build();


    when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.of(cinema));
    when(cinemaMapper.toCinemaAdminResponse(cinema)).thenReturn(expectedResponse);

    // When
    CinemaAdminResponse response = cinemaService.getCinemaById(cinemaId);

    // Then
    assertEquals(expectedResponse.getId(), response.getId());
    assertEquals(expectedResponse.getName(), response.getName());
    assertEquals(expectedResponse.getCity(), response.getCity());
    assertEquals(expectedResponse.getStreetAddress(), response.getStreetAddress());
  }

  @Test
  void getCinemaById_WhenCinemaNotFound_ThrowsEntityNotFoundException() {
    // Given
    long cinemaId = 1L;

    when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> cinemaService.getCinemaById(cinemaId));
  }
}
