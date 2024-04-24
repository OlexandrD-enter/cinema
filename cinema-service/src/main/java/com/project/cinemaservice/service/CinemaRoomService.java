package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomAdminResponse;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomCreateRequest;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomEditRequest;

/**
 * CinemaRoomService interface for managing CinemaRoom related operations.
 */
public interface CinemaRoomService {

  CinemaRoomAdminResponse createCinemaRoom(CinemaRoomCreateRequest cinemaRoomCreateRequest);

  CinemaRoomAdminResponse editCinemaRoom(Long roomId, CinemaRoomEditRequest cinemaRoomEditRequest);

  void deleteCinemaRoomById(Long cinemaRoomId);

  CinemaRoomAdminResponse getCinemaRoomById(Long cinemaRoomId);
}
