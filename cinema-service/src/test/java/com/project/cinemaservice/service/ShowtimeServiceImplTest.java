package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeAdminResponse;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeDataRequest;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeStartAndEndDate;
import com.project.cinemaservice.domain.mapper.ShowtimeMapper;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.Showtime;
import com.project.cinemaservice.persistence.repository.CinemaRoomRepository;
import com.project.cinemaservice.persistence.repository.MovieRepository;
import com.project.cinemaservice.persistence.repository.OrderTicketRepository;
import com.project.cinemaservice.persistence.repository.ShowtimeRepository;
import com.project.cinemaservice.service.exception.CinemaRoomOccupiedException;
import com.project.cinemaservice.service.impl.ShowtimeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShowtimeServiceImplTest {

  @Mock
  private ShowtimeRepository showtimeRepository;

  @Mock
  private MovieRepository movieRepository;

  @Mock
  private CinemaRoomRepository cinemaRoomRepository;

  @Mock
  private OrderTicketRepository orderTicketRepository;

  @Mock
  private ShowtimeMapper showtimeMapper;

  private ShowtimeServiceImpl showtimeService;

  @BeforeEach
  public void openMocks() {
    showtimeService = new ShowtimeServiceImpl(showtimeRepository, movieRepository,
        cinemaRoomRepository, showtimeMapper, orderTicketRepository, 5L);
  }

  @Test
  void createShowtime_Success() {
    // Given
    ShowtimeDataRequest showtimeDataRequest = new ShowtimeDataRequest();
    showtimeDataRequest.setMovieId(1L);
    showtimeDataRequest.setCinemaRoomId(1L);
    showtimeDataRequest.setStartDate(LocalDateTime.now());

    Movie movie = new Movie();
    movie.setDuration(Duration.ofMinutes(45));

    CinemaRoom cinemaRoom = new CinemaRoom();

    List<ShowtimeStartAndEndDate> existingShowTimes = new ArrayList<>(List.of(
        new ShowtimeStartAndEndDate(1L, LocalDateTime.now().minusHours(2),
            LocalDateTime.now().minusHours(1))
    ));

    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(cinemaRoom));
    when(showtimeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(showtimeMapper.toShowtimeAdminResponse(any())).thenReturn(new ShowtimeAdminResponse());
    when(showtimeRepository.findStartAndEndDateOfShowtimeByRoomId(1L)).thenReturn(
        existingShowTimes);

    // When
    ShowtimeAdminResponse response = showtimeService.createShowtime(showtimeDataRequest);

    // Then
    assertNotNull(response);
    verify(showtimeRepository, times(1)).save(any());
  }

  @Test
  void createShowtime_CinemaRoomOccupied_ThrowsCinemaRoomOccupiedException() {
    // Given
    ShowtimeDataRequest showtimeDataRequest = new ShowtimeDataRequest();
    showtimeDataRequest.setMovieId(1L);
    showtimeDataRequest.setCinemaRoomId(1L);
    showtimeDataRequest.setStartDate(LocalDateTime.now());

    Movie movie = new Movie();
    movie.setDuration(Duration.ofMinutes(45));

    CinemaRoom cinemaRoom = new CinemaRoom();

    List<ShowtimeStartAndEndDate> existingShowTimes = new ArrayList<>(List.of(
        new ShowtimeStartAndEndDate(1L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60))
    ));

    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(cinemaRoom));
    when(showtimeRepository.findStartAndEndDateOfShowtimeByRoomId(1L)).thenReturn(
        existingShowTimes);

    // When & Then
    assertThrows(CinemaRoomOccupiedException.class,
        () -> showtimeService.createShowtime(showtimeDataRequest));
    verify(showtimeRepository, never()).save(any());
  }

  @Test
  void createShowtime_MovieNotFound_ThrowsEntityNotFoundException() {
    // Given
    ShowtimeDataRequest showtimeDataRequest = new ShowtimeDataRequest();
    showtimeDataRequest.setMovieId(1L);
    showtimeDataRequest.setCinemaRoomId(1L);
    when(movieRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> showtimeService.createShowtime(showtimeDataRequest));
    verify(showtimeRepository, never()).save(any());
  }

  @Test
  void createShowtime_RoomNotFound_ThrowsEntityNotFoundException() {
    // Given
    ShowtimeDataRequest showtimeDataRequest = new ShowtimeDataRequest();
    showtimeDataRequest.setMovieId(1L);
    showtimeDataRequest.setCinemaRoomId(1L);
    when(movieRepository.findById(1L)).thenReturn(Optional.of(new Movie()));
    when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> showtimeService.createShowtime(showtimeDataRequest));
    verify(showtimeRepository, never()).save(any());
  }

  @Test
  void editShowtime_Success() {
    // Given
    ShowtimeDataRequest showtimeDataRequest = new ShowtimeDataRequest();
    showtimeDataRequest.setMovieId(1L);
    showtimeDataRequest.setCinemaRoomId(1L);
    LocalDateTime startDate = LocalDateTime.now();
    showtimeDataRequest.setStartDate(startDate);
    Movie movie = new Movie();
    movie.setDuration(Duration.ofMinutes(45));
    Showtime showtime = new Showtime();
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(new CinemaRoom()));
    when(showtimeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(showtimeMapper.toShowtimeAdminResponse(any())).thenReturn(new ShowtimeAdminResponse());

    // When
    ShowtimeAdminResponse response = showtimeService.editShowtime(1L, showtimeDataRequest);

    // Then
    assertNotNull(response);
    verify(showtimeRepository, times(1)).save(any());
  }

  @Test
  void editShowtime_ShowtimeNotFound_ThrowsEntityNotFoundException() {
    // Given
    when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> showtimeService.editShowtime(1L, new ShowtimeDataRequest()));
    verify(showtimeRepository, never()).save(any());
  }

  @Test
  void deleteShowtime_Success() {
    // Given
    Showtime showtime = new Showtime();
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

    // When
    assertDoesNotThrow(() -> showtimeService.deleteShowtimeById(1L));

    // Then
    verify(showtimeRepository, times(1)).delete(showtime);
  }

  @Test
  void deleteShowtime_ShowtimeNotFound_ThrowsEntityNotFoundException() {
    // Given
    when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> showtimeService.deleteShowtimeById(1L));
    verify(showtimeRepository, never()).delete(any());
  }

  @Test
  void getShowtimeById_Success() {
    // Given
    Showtime showtime = new Showtime();
    List<RoomSeatBriefInfo> alreadyBookedRoomSeats = new ArrayList<>(
        List.of(new RoomSeatBriefInfo(1L, 1L)));
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
    when(showtimeMapper.toShowtimeAdminResponse(showtime, alreadyBookedRoomSeats)).thenReturn(new ShowtimeAdminResponse());
    when(orderTicketRepository.findAllByTicketShowtimeAndOrderStatusReservedOrPaid(1L))
        .thenReturn(alreadyBookedRoomSeats);
    // When
    ShowtimeAdminResponse response = showtimeService.getShowtimeById(1L);

    // Then
    assertNotNull(response);
    verify(showtimeMapper, times(1)).toShowtimeAdminResponse(showtime, alreadyBookedRoomSeats);
  }

  @Test
  void getShowtimeById_ShowtimeNotFound_ThrowsEntityNotFoundException() {
    // Given
    when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> showtimeService.getShowtimeById(1L));
    verify(showtimeMapper, never()).toShowtimeAdminResponse(any());
  }
}
