package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomAdminResponse;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomCreateRequest;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomEditRequest;
import com.project.cinemaservice.domain.mapper.CinemaRoomMapper;
import com.project.cinemaservice.persistence.enums.RoomType;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.repository.CinemaRepository;
import com.project.cinemaservice.persistence.repository.CinemaRoomRepository;
import com.project.cinemaservice.service.CinemaRoomService;
import com.project.cinemaservice.service.exception.EntityAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CinemaRoomService implementation responsible for CinemaRoom related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CinemaRoomServiceImpl implements CinemaRoomService {

  private final CinemaRoomRepository cinemaRoomRepository;
  private final CinemaRepository cinemaRepository;
  private final CinemaRoomMapper cinemaRoomMapper;

  @Transactional
  @Override
  public CinemaRoomAdminResponse createCinemaRoom(CinemaRoomCreateRequest cinemaRoomCreateRequest) {
    String cinemaRoomName = cinemaRoomCreateRequest.getName();
    Long cinemaId = cinemaRoomCreateRequest.getCinemaId();

    log.debug("Creating cinemaRoom with name {}", cinemaRoomName);

    checkIfRoomExistByNameAndCinemaId(cinemaRoomName, cinemaId);
    Cinema cinema = cinemaRepository.findById(cinemaId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Cinema with id=%d not found", cinemaId)));

    CinemaRoom cinemaRoom = cinemaRoomMapper.toCinemaRoomEntity(cinemaRoomCreateRequest);
    cinemaRoom.setCinema(cinema);
    CinemaRoom savedCinemaRoom = cinemaRoomRepository.save(cinemaRoom);

    log.debug("Created cinemaRoom with name {}", cinemaRoomName);

    return cinemaRoomMapper.toCinemaRoomAdminResponse(savedCinemaRoom);
  }

  @Transactional
  @Override
  public CinemaRoomAdminResponse editCinemaRoom(Long roomId,
      CinemaRoomEditRequest cinemaRoomEditRequest) {
    log.debug("Updating cinemaRoom with id {}", roomId);

    CinemaRoom cinemaRoom = findCinemaRoomEntityById(roomId);
    cinemaRoom.setName(cinemaRoomEditRequest.getName());
    cinemaRoom.setRoomType(RoomType.valueOf(cinemaRoomEditRequest.getRoomType()));

    CinemaRoom savedCinemaRoom = cinemaRoomRepository.save(cinemaRoom);

    log.debug("Updated cinemaRoom with id {}", roomId);

    return cinemaRoomMapper.toCinemaRoomAdminResponse(savedCinemaRoom);
  }

  @Transactional
  @Override
  public void deleteCinemaRoomById(Long cinemaRoomId) {
    log.debug("Trying to delete cinemaRoom with id {}", cinemaRoomId);

    CinemaRoom cinemaRoom = findCinemaRoomEntityById(cinemaRoomId);

    cinemaRoomRepository.delete(cinemaRoom);

    log.debug("Deleted cinemaRoom with id {}", cinemaRoom);
  }

  @Transactional(readOnly = true)
  @Override
  public CinemaRoomAdminResponse getCinemaRoomById(Long cinemaRoomId) {
    CinemaRoom cinemaRoom = findCinemaRoomEntityById(cinemaRoomId);

    return cinemaRoomMapper.toCinemaRoomAdminResponse(cinemaRoom);
  }

  private void checkIfRoomExistByNameAndCinemaId(String roomName, Long cinemaId) {
    if (cinemaRoomRepository.findByNameAndCinemaId(roomName,
        cinemaId).isPresent()) {
      throw new EntityAlreadyExistsException(
          String.format("CinemaRoom with name %s and cinemaId %d exist", roomName, cinemaId));
    }
  }

  private CinemaRoom findCinemaRoomEntityById(Long cinemaRoomId) {
    return cinemaRoomRepository.findById(cinemaRoomId).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("CinemaRoom with id=%d not found", cinemaRoomId)));
  }
}
