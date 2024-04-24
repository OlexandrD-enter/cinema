package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.cinema.CinemaAdminResponse;
import com.project.cinemaservice.domain.dto.cinema.CinemaDataRequest;

/**
 * CinemaService interface for managing Cinema related operations.
 */
public interface CinemaService {

  CinemaAdminResponse createCinema(CinemaDataRequest cinemaDataRequest);

  CinemaAdminResponse editCinema(Long cinemaId, CinemaDataRequest cinemaDataRequest);

  void deleteCinemaById(Long cinemaId);

  CinemaAdminResponse getCinemaById(Long cinemaId);
}
