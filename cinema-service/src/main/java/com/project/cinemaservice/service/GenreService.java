package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.genre.GenreAdminBriefResponse;
import com.project.cinemaservice.domain.dto.genre.GenreAdminResponse;
import com.project.cinemaservice.domain.dto.genre.GenreDataRequest;
import java.util.List;

/**
 * GenreService interface for managing Genre related operations.
 */
public interface GenreService {

  GenreAdminResponse createGenre(GenreDataRequest genreDataRequest);

  GenreAdminResponse editGenre(Long genreId, GenreDataRequest genreDataRequest);

  void deleteGenreById(Long genreId);

  GenreAdminResponse getGenreById(Long genreId);

  List<GenreAdminBriefResponse> getGenres();
}
