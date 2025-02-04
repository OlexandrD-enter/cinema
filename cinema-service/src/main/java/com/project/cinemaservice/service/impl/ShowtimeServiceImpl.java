package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeAdminResponse;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeClientResponse;
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
import com.project.cinemaservice.service.ShowtimeService;
import com.project.cinemaservice.service.exception.CinemaRoomOccupiedException;
import com.project.cinemaservice.service.exception.ShowtimeActionException;
import com.project.cinemaservice.service.exception.ShowtimeAlreadyStartedException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ShowtimeService implementation responsible for showtime related operations.
 */
@Service
@Slf4j
public class ShowtimeServiceImpl implements ShowtimeService {

  private final ShowtimeRepository showtimeRepository;
  private final MovieRepository movieRepository;
  private final CinemaRoomRepository cinemaRoomRepository;
  private final OrderTicketRepository orderTicketRepository;
  private final ShowtimeMapper showtimeMapper;
  private final long delayBetweenMovieShowtimeInMinutes;

  /**
   * Constructs a ShowtimeServiceImpl with necessary repositories, mapper, and delay between movie
   * showtimes.
   *
   * @param showtimeRepository                 The repository for managing showtimes.
   * @param movieRepository                    The repository for managing movies.
   * @param cinemaRoomRepository               The repository for managing cinema rooms.
   * @param showtimeMapper                     The mapper for converting between entities and DTOs.
   * @param delayBetweenMovieShowtimeInMinutes The delay between consecutive showtimes of movies.
   */
  public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository,
      MovieRepository movieRepository,
      CinemaRoomRepository cinemaRoomRepository,
      ShowtimeMapper showtimeMapper, OrderTicketRepository orderTicketRepository,
      @Value("${movies.delay-between-showtime}") long delayBetweenMovieShowtimeInMinutes) {
    this.showtimeRepository = showtimeRepository;
    this.movieRepository = movieRepository;
    this.cinemaRoomRepository = cinemaRoomRepository;
    this.showtimeMapper = showtimeMapper;
    this.orderTicketRepository = orderTicketRepository;
    this.delayBetweenMovieShowtimeInMinutes = delayBetweenMovieShowtimeInMinutes;
  }

  @Transactional
  @Override
  public ShowtimeAdminResponse createShowtime(ShowtimeDataRequest showtimeDataRequest) {
    Long cinemaRoomId = showtimeDataRequest.getCinemaRoomId();
    LocalDateTime startDate = showtimeDataRequest.getStartDate();

    log.debug("Creating Showtime for movie {} in cinemaRoom {}", showtimeDataRequest.getMovieId(),
        cinemaRoomId);

    Movie movie = findMovieEntityById(showtimeDataRequest.getMovieId());
    CinemaRoom cinemaRoom = findCinemaRoomEntityById(showtimeDataRequest.getCinemaRoomId());
    LocalDateTime endDate = startDate.plus(movie.getDuration());

    Showtime showtime = Showtime.builder()
        .movie(movie)
        .cinemaRoom(cinemaRoom)
        .price(showtimeDataRequest.getPrice())
        .startDate(startDate)
        .endDate(endDate)
        .build();

    checkIfCinemaRoomAvailable(cinemaRoomId, startDate, endDate);

    Showtime savedShowtime = showtimeRepository.save(showtime);

    log.debug("Created Showtime for movie {} in cinemaRoom {}", showtimeDataRequest.getMovieId(),
        cinemaRoomId);

    return showtimeMapper.toShowtimeAdminResponse(savedShowtime);
  }

  @Transactional
  @Override
  public ShowtimeAdminResponse editShowtime(Long showtimeId,
      ShowtimeDataRequest showtimeDataRequest) {

    List<RoomSeatBriefInfo> bookedSeatsByShowtime =
        orderTicketRepository.findAllByTicketShowtimeAndOrderStatusReservedOrPaid(showtimeId);

    if (!bookedSeatsByShowtime.isEmpty()) {
      //TODO refund payments for users which booked this showtime or notify them about changes
      throw new ShowtimeActionException("Showtime already booked by users, it can`t be updated");
    }

    Long cinemaRoomId = showtimeDataRequest.getCinemaRoomId();
    LocalDateTime startDate = showtimeDataRequest.getStartDate();

    log.debug("Updating Showtime for movie {} in cinemaRoom {}", showtimeDataRequest.getMovieId(),
        cinemaRoomId);

    Showtime showtime = findShowtimeEntityById(showtimeId);

    Movie movie = findMovieEntityById(showtimeDataRequest.getMovieId());
    CinemaRoom cinemaRoom = findCinemaRoomEntityById(showtimeDataRequest.getCinemaRoomId());
    LocalDateTime endDate = startDate.plus(movie.getDuration());

    checkIfCinemaRoomAvailable(cinemaRoomId, startDate, endDate);

    showtime.setMovie(movie);
    showtime.setCinemaRoom(cinemaRoom);
    showtime.setPrice(showtimeDataRequest.getPrice());
    showtime.setStartDate(startDate);
    showtime.setEndDate(startDate.plus(movie.getDuration()));

    Showtime savedShowtime = showtimeRepository.save(showtime);

    log.debug("Updated Showtime for movie {} in cinemaRoom {}", showtimeDataRequest.getMovieId(),
        cinemaRoomId);

    return showtimeMapper.toShowtimeAdminResponse(savedShowtime);
  }

  @Transactional
  @Override
  public void deleteShowtimeById(Long showtimeId) {
    Showtime showtime = findShowtimeEntityById(showtimeId);

    List<RoomSeatBriefInfo> bookedSeatsByShowtime =
        orderTicketRepository.findAllByTicketShowtimeAndOrderStatusReservedOrPaid(showtimeId);

    if (!bookedSeatsByShowtime.isEmpty()) {
      //TODO refund payments for users which booked this showtime and notify them
      throw new ShowtimeActionException("Showtime already booked by users, it can`t be deleted");
    }

    showtimeRepository.delete(showtime);
  }

  @Transactional
  @Override
  public ShowtimeAdminResponse getShowtimeByIdForAdmin(Long showtimeId) {
    Showtime showtime = findShowtimeEntityById(showtimeId);
    List<RoomSeatBriefInfo> bookedSeatsByShowtime =
        orderTicketRepository.findAllByTicketShowtimeAndOrderStatusReservedOrPaid(showtimeId);

    return showtimeMapper.toShowtimeAdminResponse(showtime, bookedSeatsByShowtime);
  }

  @Transactional
  @Override
  public ShowtimeClientResponse getShowtimeByIdForClient(Long showtimeId) {
    Showtime showtime = findShowtimeEntityById(showtimeId);

    if (showtime.getStartDate().isBefore(LocalDateTime.now())) {
      throw new ShowtimeAlreadyStartedException(
          String.format("Showtime with id=%d already passed", showtimeId));
    }

    List<RoomSeatBriefInfo> bookedSeatsByShowtime =
        orderTicketRepository.findAllByTicketShowtimeAndOrderStatusReservedOrPaid(showtimeId);

    return showtimeMapper.toShowtimeClientResponse(showtime, bookedSeatsByShowtime);
  }

  private Showtime findShowtimeEntityById(Long showtimeId) {
    return showtimeRepository.findById(showtimeId).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("Showtime with id=%d not found", showtimeId)));
  }

  private void checkIfCinemaRoomAvailable(Long cinemaRoomId, LocalDateTime startDate,
      LocalDateTime endDate) {
    List<ShowtimeStartAndEndDate> existingShowTimes =
        showtimeRepository.findStartAndEndDateOfShowtimeByRoomId(cinemaRoomId);

    boolean isOverlap = existingShowTimes.stream()
        .anyMatch(existingShowtime ->
            startDate.minusMinutes(delayBetweenMovieShowtimeInMinutes)
                .isBefore(existingShowtime.getEndDate())
                && endDate.isAfter(existingShowtime.getStartDate()));

    if (isOverlap) {
      throw new CinemaRoomOccupiedException(
          String.format("CinemaRoom with id=%d already occupied for time %s", cinemaRoomId,
              startDate));
    }
  }

  private Movie findMovieEntityById(Long movieId) {
    return movieRepository.findById(movieId).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("Movie with id=%d not found", movieId)));
  }

  private CinemaRoom findCinemaRoomEntityById(Long cinemaRoomId) {
    return cinemaRoomRepository.findById(cinemaRoomId)
        .orElseThrow(
            () -> new EntityNotFoundException(
                String.format("CinemaRoom with id=%d not found", cinemaRoomId)));
  }
}
