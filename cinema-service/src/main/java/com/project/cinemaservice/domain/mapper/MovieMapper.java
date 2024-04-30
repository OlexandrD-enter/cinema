package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieCreateRequest;
import com.project.cinemaservice.persistence.model.Genre;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.MovieGenre;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface responsible for mapping between Movie entity and related DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {

  Movie toMovieEntity(MovieCreateRequest movieCreateRequest);

  @Mapping(source = "auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "auditEntity.modifiedBy", target = "modifiedBy")
  MovieAdminResponse toMovieAdminResponse(Movie movie);

  /**
   * Maps a list of MovieGenre entities to a list of genre names.
   *
   * @param movieGenres The list of MovieGenre entities to map
   * @return A list of genre names
   */
  default List<String> mapMovieGenres(List<MovieGenre> movieGenres) {
    return movieGenres.stream()
        .map(MovieGenre::getGenre)
        .map(Genre::getName)
        .toList();
  }
}
