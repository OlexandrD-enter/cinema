package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatAdminResponse;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatCreateRequest;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatEditRequest;

/**
 * CinemaService interface for managing RoomSeat related operations.
 */
public interface RoomSeatService {
  RoomSeatAdminResponse createRoomSeat(RoomSeatCreateRequest roomSeatDataRequest);

  RoomSeatAdminResponse editRoomSeat(Long roomSeatId, RoomSeatEditRequest roomSeatEditRequest);

  void deleteRoomSeatById(Long roomSeatId);

  RoomSeatAdminResponse getRoomSeatById(Long roomSeatId);
}
