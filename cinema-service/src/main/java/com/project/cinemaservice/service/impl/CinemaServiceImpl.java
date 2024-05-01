package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.cinema.CinemaAdminResponse;
import com.project.cinemaservice.domain.dto.cinema.CinemaDataRequest;
import com.project.cinemaservice.domain.mapper.CinemaMapper;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.repository.CinemaRepository;
import com.project.cinemaservice.service.CinemaService;
import com.project.cinemaservice.service.exception.CinemaAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CinemaService implementation responsible for cinema related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CinemaServiceImpl implements CinemaService {

  private final CinemaRepository cinemaRepository;
  private final CinemaMapper cinemaMapper;

  @Transactional
  @Override
  public CinemaAdminResponse createCinema(CinemaDataRequest cinemaDataRequest) {
    String cinemaName = cinemaDataRequest.getName();
    log.debug("Creating cinema with name {}", cinemaName);

    checkIfCinemaExistByName(cinemaName);

    Cinema cinema = cinemaMapper.toCinemaEntity(cinemaDataRequest);
    Cinema savedCinema = cinemaRepository.save(cinema);

    log.debug("Created cinema with name {}", cinemaName);

    return cinemaMapper.toCinemaAdminResponse(savedCinema);
  }

  @Transactional
  @Override
  public CinemaAdminResponse editCinema(Long cinemaId, CinemaDataRequest cinemaDataRequest) {
    String cinemaName = cinemaDataRequest.getName();
    log.debug("Updating cinema with id {}", cinemaId);

    checkIfCinemaExistByName(cinemaName);

    Cinema cinema = findCinemaEntityById(cinemaId);

    cinema.setName(cinemaDataRequest.getName());
    cinema.setCity(cinemaDataRequest.getCity());
    cinema.setStreetAddress(cinemaDataRequest.getStreetAddress());

    Cinema savedCinema = cinemaRepository.save(cinema);

    log.debug("Updated cinema with id {}", cinemaId);

    return cinemaMapper.toCinemaAdminResponse(savedCinema);
  }

  @Transactional
  @Override
  public void deleteCinemaById(Long cinemaId) {
    log.debug("Trying to delete cinema with id {}", cinemaId);

    Cinema cinema = findCinemaEntityById(cinemaId);

    cinemaRepository.delete(cinema);

    log.debug("Deleted cinema with id {}", cinemaId);
  }

  @Transactional(readOnly = true)
  @Override
  public CinemaAdminResponse getCinemaById(Long cinemaId) {
    Cinema cinema = findCinemaEntityById(cinemaId);

    return cinemaMapper.toCinemaAdminResponse(cinema);
  }

  private void checkIfCinemaExistByName(String cinemaName) {
    if (cinemaRepository.findByName(cinemaName).isPresent()) {
      throw new CinemaAlreadyExistsException(
          String.format("Cinema with name='%s' already exists", cinemaName));
    }
  }

  private Cinema findCinemaEntityById(Long cinemaId) {
    return cinemaRepository.findById(cinemaId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Cinema with id=%d not found", cinemaId)));
  }
}
