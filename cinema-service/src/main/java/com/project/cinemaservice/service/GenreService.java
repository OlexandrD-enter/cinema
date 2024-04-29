package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.genre.GenreAdminBriefResponse;
import com.project.cinemaservice.domain.dto.genre.GenreAdminResponse;
import com.project.cinemaservice.domain.dto.genre.GenreDataRequest;
import java.util.List;

public interface GenreService {

  GenreAdminResponse createCinema(GenreDataRequest genreDataRequest);

  GenreAdminResponse editCinema(Long genreId, GenreDataRequest genreDataRequest);

  void deleteGenreById(Long genreId);

  GenreAdminResponse getGenreById(Long genreId);

  List<GenreAdminBriefResponse> getGenres();
}
