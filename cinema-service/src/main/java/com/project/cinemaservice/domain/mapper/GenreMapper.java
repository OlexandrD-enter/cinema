package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.genre.GenreAdminBriefResponse;
import com.project.cinemaservice.domain.dto.genre.GenreAdminResponse;
import com.project.cinemaservice.domain.dto.genre.GenreDataRequest;
import com.project.cinemaservice.persistence.model.Genre;
import org.mapstruct.Mapper;

/**
 * Mapper interface responsible for mapping between Genre entity and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface GenreMapper {

  Genre toGenreEntity(GenreDataRequest genreDataRequest);

  GenreAdminResponse toGenreAdminResponse(Genre genre);

  GenreAdminBriefResponse toGenreAdminBriefResponse(Genre genre);
}
