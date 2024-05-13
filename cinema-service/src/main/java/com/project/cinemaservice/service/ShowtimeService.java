package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.showtime.ShowtimeAdminResponse;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeClientResponse;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeDataRequest;

/**
 * ShowtimeService interface for managing Showtime related operations.
 */
public interface ShowtimeService {

  ShowtimeAdminResponse createShowtime(ShowtimeDataRequest showtimeDataRequest);

  ShowtimeAdminResponse editShowtime(Long showtimeId, ShowtimeDataRequest showtimeDataRequest);

  void deleteShowtimeById(Long showtimeId);

  ShowtimeAdminResponse getShowtimeByIdForAdmin(Long showtimeId);

  ShowtimeClientResponse getShowtimeByIdForClient(Long showtimeId);
}
