package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomAdminResponse;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomCreateRequest;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomEditRequest;
import com.project.cinemaservice.domain.mapper.CinemaRoomMapper;
import com.project.cinemaservice.persistence.enums.RoomType;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.repository.CinemaRepository;
import com.project.cinemaservice.persistence.repository.CinemaRoomRepository;
import com.project.cinemaservice.service.exception.EntityAlreadyExistsException;
import com.project.cinemaservice.service.impl.CinemaRoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CinemaRoomServiceImplTest {

  @Mock
  private CinemaRoomRepository cinemaRoomRepository;

  @Mock
  private CinemaRepository cinemaRepository;

  @Mock
  private CinemaRoomMapper cinemaRoomMapper;

  @InjectMocks
  private CinemaRoomServiceImpl cinemaRoomService;

  @Test
  void createCinemaRoom_Success() {
    // Given
    String roomName = "Room 1";
    Long cinemaId = 1L;
    CinemaRoomCreateRequest request = new CinemaRoomCreateRequest(roomName, RoomType.VIP.name(),
        cinemaId);
    Cinema cinema = Cinema.builder()
        .id(cinemaId)
        .name("Cinema")
        .city("City")
        .streetAddress("Address, 1")
        .build();
    CinemaRoom cinemaRoom = CinemaRoom.builder()
        .name(roomName)
        .roomType(RoomType.VIP)
        .cinema(cinema)
        .build();
    CinemaRoomAdminResponse expectedResponse = CinemaRoomAdminResponse.builder()
        .cinemaId(cinema.getId())
        .name(cinemaRoom.getName())
        .roomType(cinemaRoom.getRoomType().name())
        .build();

    when(cinemaRepository.findById(cinemaId)).thenReturn(Optional.of(cinema));
    when(cinemaRoomRepository.findByNameAndCinemaId(roomName, cinemaId)).thenReturn(
        Optional.empty());
    when(cinemaRoomMapper.toCinemaRoomEntity(request)).thenReturn(cinemaRoom);
    when(cinemaRoomRepository.save(cinemaRoom)).thenReturn(cinemaRoom);
    when(cinemaRoomMapper.toCinemaRoomAdminResponse(cinemaRoom)).thenReturn(expectedResponse);

    // When
    CinemaRoomAdminResponse response = cinemaRoomService.createCinemaRoom(request);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void createCinemaRoom_WhenCinemaNotFound_ThrowsEntityNotFoundException() {
    // Given
    CinemaRoomCreateRequest request = new CinemaRoomCreateRequest("Room 1", RoomType.VIP.name(),
        1L);
    when(cinemaRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> cinemaRoomService.createCinemaRoom(request));
  }

  @Test
  void createCinemaRoom_WhenRoomAlreadyExists_ThrowsEntityAlreadyExistsException() {
    // Given
    String roomName = "Room 1";
    Long cinemaId = 1L;
    CinemaRoomCreateRequest request = new CinemaRoomCreateRequest(roomName, RoomType.VIP.name(),
        cinemaId);

    when(cinemaRoomRepository.findByNameAndCinemaId(roomName, cinemaId)).thenReturn(
        Optional.of(new CinemaRoom()));

    // When & Then
    assertThrows(EntityAlreadyExistsException.class,
        () -> cinemaRoomService.createCinemaRoom(request));
  }

  @Test
  void editCinemaRoom_Success() {
    // Given
    long roomId = 1L;
    String expectedName = "Room 2";
    CinemaRoomEditRequest request = new CinemaRoomEditRequest(expectedName, RoomType.VIP.name());
    CinemaRoom cinemaRoom = CinemaRoom.builder()
        .id(roomId)
        .name("Room 1")
        .roomType(RoomType.VIP)
        .build();

    CinemaRoomAdminResponse expectedResponse = CinemaRoomAdminResponse.builder()
        .name(expectedName)
        .roomType(RoomType.VIP.name())
        .build();

    when(cinemaRoomRepository.findById(roomId)).thenReturn(Optional.of(cinemaRoom));
    when(cinemaRoomRepository.save(cinemaRoom)).thenReturn(cinemaRoom);
    when(cinemaRoomMapper.toCinemaRoomAdminResponse(cinemaRoom)).thenReturn(expectedResponse);

    // When
    CinemaRoomAdminResponse response = cinemaRoomService.editCinemaRoom(roomId, request);

    // Then
    assertEquals(expectedResponse.getRoomType(), response.getRoomType());
    assertEquals(expectedResponse.getName(), response.getName());
  }

  @Test
  void editCinemaRoom_WhenRoomNotFound_ThrowsEntityNotFoundException() {
    // Given
    long roomId = 1L;
    CinemaRoomEditRequest request = new CinemaRoomEditRequest("Room 1", RoomType.VIP.name());
    when(cinemaRoomRepository.findById(roomId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> cinemaRoomService.editCinemaRoom(roomId, request));
  }

  @Test
  void deleteCinemaRoomById_Success() {
    // Given
    long roomId = 1L;
    CinemaRoom cinemaRoom = CinemaRoom.builder()
        .id(roomId)
        .name("Room 1")
        .roomType(RoomType.VIP)
        .build();

    when(cinemaRoomRepository.findById(roomId)).thenReturn(Optional.of(cinemaRoom));

    // When
    cinemaRoomService.deleteCinemaRoomById(roomId);

    // Then
    verify(cinemaRoomRepository, times(1)).delete(cinemaRoom);
  }

  @Test
  void deleteCinemaRoomById_WhenRoomNotFound_ThrowsEntityNotFoundException() {
    // Given
    long roomId = 1L;
    when(cinemaRoomRepository.findById(roomId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> cinemaRoomService.deleteCinemaRoomById(roomId));
  }

  @Test
  void getCinemaRoomById_Success() {
    // Given
    long roomId = 1L;
    CinemaRoom cinemaRoom = CinemaRoom.builder()
        .id(roomId)
        .name("Room 1")
        .roomType(RoomType.VIP)
        .build();

    CinemaRoomAdminResponse expectedResponse = CinemaRoomAdminResponse.builder()
        .name(cinemaRoom.getName())
        .roomType(cinemaRoom.getRoomType().name())
        .build();

    when(cinemaRoomRepository.findById(roomId)).thenReturn(Optional.of(cinemaRoom));
    when(cinemaRoomMapper.toCinemaRoomAdminResponse(cinemaRoom)).thenReturn(expectedResponse);

    // When
    CinemaRoomAdminResponse response = cinemaRoomService.getCinemaRoomById(roomId);

    // Then
    assertEquals(expectedResponse, response);
  }

  @Test
  void getCinemaRoomById_WhenRoomNotFound_ThrowsEntityNotFoundException() {
    // Given
    long roomId = 1L;
    when(cinemaRoomRepository.findById(roomId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> cinemaRoomService.getCinemaRoomById(roomId));
  }
}
