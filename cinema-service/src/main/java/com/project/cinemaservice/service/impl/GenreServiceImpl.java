package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.genre.GenreAdminBriefResponse;
import com.project.cinemaservice.domain.dto.genre.GenreAdminResponse;
import com.project.cinemaservice.domain.dto.genre.GenreDataRequest;
import com.project.cinemaservice.domain.mapper.GenreMapper;
import com.project.cinemaservice.persistence.model.Genre;
import com.project.cinemaservice.persistence.repository.GenreRepository;
import com.project.cinemaservice.service.GenreService;
import com.project.cinemaservice.service.exception.GenreAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GenreService implementation responsible for genre related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepository;
  private final GenreMapper genreMapper;

  @Transactional
  @Override
  public GenreAdminResponse createCinema(GenreDataRequest genreDataRequest) {
    String genreName = genreDataRequest.getName();
    log.debug("Creating genre with name {}", genreName);

    checkIfGenreExistByName(genreName);

    Genre genre = genreMapper.toGenreEntity(genreDataRequest);
    Genre savedCinema = genreRepository.save(genre);

    log.debug("Created genre with name {}", genreName);

    return genreMapper.toGenreAdminResponse(savedCinema);
  }

  @Transactional
  @Override
  public GenreAdminResponse editCinema(Long genreId, GenreDataRequest genreDataRequest) {
    String genreName = genreDataRequest.getName();
    log.debug("Updating genre with id {}", genreId);

    checkIfGenreExistByName(genreName);

    Genre genre = findGenreEntityById(genreId);

    genre.setName(genreDataRequest.getName());

    Genre savedGenre = genreRepository.save(genre);

    log.debug("Updated genre with id {}", genreId);

    return genreMapper.toGenreAdminResponse(savedGenre);
  }

  @Transactional
  @Override
  public void deleteGenreById(Long genreId) {
    log.debug("Trying to delete genre with id {}", genreId);

    Genre genre = findGenreEntityById(genreId);

    genreRepository.delete(genre);

    log.debug("Deleted genre with id {}", genreId);
  }

  @Transactional(readOnly = true)
  @Override
  public GenreAdminResponse getGenreById(Long genreId) {
    Genre genre = findGenreEntityById(genreId);

    return genreMapper.toGenreAdminResponse(genre);
  }

  @Transactional(readOnly = true)
  @Override
  public List<GenreAdminBriefResponse> getGenres() {
    return genreRepository.findAll().stream()
        .map(genreMapper::toGenreAdminBriefResponse)
        .toList();
  }

  private void checkIfGenreExistByName(String genreName) {
    if (genreRepository.findByName(genreName).isPresent()) {
      throw new GenreAlreadyExistsException(
          String.format("Genre with name='%s' already exists", genreName));
    }
  }

  private Genre findGenreEntityById(Long genreId) {
    return genreRepository.findById(genreId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Genre with id=%d not found", genreId)));
  }
}
