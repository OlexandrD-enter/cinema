package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatAdminResponse;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatCreateRequest;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatEditRequest;
import com.project.cinemaservice.domain.mapper.RoomSeatMapper;
import com.project.cinemaservice.persistence.enums.RoomType;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.model.RoomSeat;
import com.project.cinemaservice.persistence.repository.CinemaRoomRepository;
import com.project.cinemaservice.persistence.repository.RoomSeatRepository;
import com.project.cinemaservice.service.execption.SeatNumberAlreadyExistsException;
import com.project.cinemaservice.service.impl.RoomSeatServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoomSeatServiceImplTest {

  @Mock
  private RoomSeatRepository roomSeatRepository;

  @Mock
  private CinemaRoomRepository cinemaRoomRepository;

  @Mock
  private RoomSeatMapper roomSeatMapper;

  @InjectMocks
  private RoomSeatServiceImpl roomSeatService;

  @Test
  void createRoomSeat_Success() {
    // Given
    RoomSeatCreateRequest request = new RoomSeatCreateRequest(10L, 1L);
    CinemaRoom cinemaRoom = CinemaRoom.builder()
        .id(1L)
        .name("Room 1")
        .roomType(RoomType.VIP)
        .build();
    RoomSeat roomSeat = RoomSeat.builder()
        .id(1L)
        .seatNumber(10L)
        .cinemaRoom(cinemaRoom)
        .build();
    RoomSeatAdminResponse expectedResponse = new RoomSeatAdminResponse(1L, 10L, 1L);

    when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(cinemaRoom));
    when(roomSeatRepository.findBySeatNumberAndCinemaRoomId(10L, 1L)).thenReturn(Optional.empty());
    when(roomSeatMapper.toRoomSeatEntity(request, cinemaRoom)).thenReturn(roomSeat);
    when(roomSeatRepository.save(roomSeat)).thenReturn(roomSeat);
    when(roomSeatMapper.toRoomSeatAdminResponse(roomSeat)).thenReturn(expectedResponse);

    // When
    RoomSeatAdminResponse response = roomSeatService.createRoomSeat(request);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void createRoomSeat_WhenCinemaRoomNotFound_ThrowsEntityNotFoundException() {
    // Given
    RoomSeatCreateRequest request = new RoomSeatCreateRequest(1L, 1L);
    when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> roomSeatService.createRoomSeat(request));
  }

  @Test
  void createRoomSeat_WhenSeatNumberAlreadyExists_ThrowsSeatNumberAlreadyExistsException() {
    // Given
    RoomSeatCreateRequest request = new RoomSeatCreateRequest(1L, 1L);
    when(roomSeatRepository.findBySeatNumberAndCinemaRoomId(1L, 1L)).thenReturn(
        Optional.of(new RoomSeat()));

    // When & Then
    assertThrows(SeatNumberAlreadyExistsException.class,
        () -> roomSeatService.createRoomSeat(request));
  }

  @Test
  void editRoomSeat_Success() {
    // Given
    long roomSeatId = 1L;
    RoomSeatEditRequest request = new RoomSeatEditRequest(2L);
    RoomSeat roomSeat = RoomSeat.builder()
        .id(1L)
        .seatNumber(10L)
        .build();
    RoomSeatAdminResponse expectedResponse = new RoomSeatAdminResponse(roomSeatId, 2L, 1L);

    when(roomSeatRepository.findById(roomSeatId)).thenReturn(Optional.of(roomSeat));
    when(roomSeatRepository.save(roomSeat)).thenReturn(roomSeat);
    when(roomSeatMapper.toRoomSeatAdminResponse(roomSeat)).thenReturn(expectedResponse);

    // When
    RoomSeatAdminResponse response = roomSeatService.editRoomSeat(roomSeatId, request);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void editRoomSeat_WhenRoomSeatNotFound_ThrowsEntityNotFoundException() {
    // Given
    long roomSeatId = 1L;
    RoomSeatEditRequest request = new RoomSeatEditRequest(2L);
    when(roomSeatRepository.findById(roomSeatId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> roomSeatService.editRoomSeat(roomSeatId, request));
  }

  @Test
  void deleteRoomSeatById_Success() {
    // Given
    long roomSeatId = 1L;
    RoomSeat roomSeat = RoomSeat.builder()
        .id(1L)
        .seatNumber(10L)
        .build();

    when(roomSeatRepository.findById(roomSeatId)).thenReturn(Optional.of(roomSeat));

    // When
    roomSeatService.deleteRoomSeatById(roomSeatId);

    // Then
    verify(roomSeatRepository, times(1)).delete(roomSeat);
  }

  @Test
  void deleteRoomSeatById_WhenRoomSeatNotFound_ThrowsEntityNotFoundException() {
    // Given
    long roomSeatId = 1L;
    when(roomSeatRepository.findById(roomSeatId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> roomSeatService.deleteRoomSeatById(roomSeatId));
  }

  @Test
  void getRoomSeatById_Success() {
    // Given
    long roomSeatId = 1L;
    CinemaRoom cinemaRoom = CinemaRoom.builder()
        .id(1L)
        .name("Room 1")
        .roomType(RoomType.VIP)
        .build();
    RoomSeat roomSeat = RoomSeat.builder()
        .id(1L)
        .seatNumber(10L)
        .cinemaRoom(cinemaRoom)
        .build();
    RoomSeatAdminResponse expectedResponse = new RoomSeatAdminResponse(roomSeatId, 1L, 1L);

    when(roomSeatRepository.findById(roomSeatId)).thenReturn(Optional.of(roomSeat));
    when(roomSeatMapper.toRoomSeatAdminResponse(roomSeat)).thenReturn(expectedResponse);

    // When
    RoomSeatAdminResponse response = roomSeatService.getRoomSeatById(roomSeatId);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void getRoomSeatById_WhenRoomSeatNotFound_ThrowsEntityNotFoundException() {
    // Given
    long roomSeatId = 1L;
    when(roomSeatRepository.findById(roomSeatId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> roomSeatService.getRoomSeatById(roomSeatId));
  }
}
