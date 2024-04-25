package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatAdminResponse;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatCreateRequest;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatEditRequest;
import com.project.cinemaservice.domain.mapper.RoomSeatMapper;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.model.RoomSeat;
import com.project.cinemaservice.persistence.repository.CinemaRoomRepository;
import com.project.cinemaservice.persistence.repository.RoomSeatRepository;
import com.project.cinemaservice.service.RoomSeatService;
import com.project.cinemaservice.service.execption.SeatNumberAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * RoomSeatService implementation responsible for room seats related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoomSeatServiceImpl implements RoomSeatService {

  private final RoomSeatRepository roomSeatRepository;
  private final CinemaRoomRepository cinemaRoomRepository;
  private final RoomSeatMapper roomSeatMapper;

  @Override
  public RoomSeatAdminResponse createRoomSeat(RoomSeatCreateRequest roomSeatDataRequest) {
    Long seatNumber = roomSeatDataRequest.getSeatNumber();
    Long roomId = roomSeatDataRequest.getCinemaRoomId();

    log.debug("Creating RoomSeat with seatNumber {} in room {}", seatNumber, roomId);

    checkIfSeatRoomExistBySeatNumberAndRoomId(seatNumber, roomId);
    CinemaRoom cinemaRoom = cinemaRoomRepository.findById(roomId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Room with id=%d not found", roomId)));

    RoomSeat roomSeat = roomSeatMapper.toRoomSeatEntity(roomSeatDataRequest, cinemaRoom);
    RoomSeat savedRoomSeat = roomSeatRepository.save(roomSeat);

    log.debug("Created RoomSeat with seatNumber {} in room {}", seatNumber, roomId);

    return roomSeatMapper.toRoomSeatAdminResponse(savedRoomSeat);
  }

  @Override
  public RoomSeatAdminResponse editRoomSeat(Long roomSeatId,
      RoomSeatEditRequest roomSeatEditRequest) {
    log.debug("Updating RoomSeat with id {}", roomSeatId);

    RoomSeat roomSeat = findRoomSeatEntityById(roomSeatId);
    roomSeat.setSeatNumber(roomSeatEditRequest.getSeatNumber());

    RoomSeat savedRoomSeat = roomSeatRepository.save(roomSeat);

    log.debug("Updated RoomSeat with id {}", roomSeatId);

    return roomSeatMapper.toRoomSeatAdminResponse(savedRoomSeat);
  }

  @Override
  public void deleteRoomSeatById(Long roomSeatId) {
    log.debug("Trying to delete RoomSeat with id {}", roomSeatId);

    RoomSeat roomSeat = findRoomSeatEntityById(roomSeatId);

    roomSeatRepository.delete(roomSeat);

    log.debug("Deleted RoomSeat with id {}", roomSeatId);
  }

  @Override
  public RoomSeatAdminResponse getRoomSeatById(Long roomSeatId) {
    RoomSeat roomSeat = findRoomSeatEntityById(roomSeatId);

    return roomSeatMapper.toRoomSeatAdminResponse(roomSeat);
  }

  private void checkIfSeatRoomExistBySeatNumberAndRoomId(Long seatNumber, Long roomId) {
    if (roomSeatRepository.findBySeatNumberAndCinemaRoomId(seatNumber,
        roomId).isPresent()) {
      throw new SeatNumberAlreadyExistsException(
          String.format("RoomSeat with seat number %d and roomId %d exist", seatNumber, roomId));
    }
  }

  private RoomSeat findRoomSeatEntityById(Long roomSeatId) {
    return roomSeatRepository.findById(roomSeatId).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("RoomSeat with id=%d not found", roomSeatId)));
  }
}
