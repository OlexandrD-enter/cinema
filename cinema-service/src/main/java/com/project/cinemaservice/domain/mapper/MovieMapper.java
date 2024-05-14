package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.movie.MovieAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MovieBriefInfo;
import com.project.cinemaservice.domain.dto.movie.MovieClientResponse;
import com.project.cinemaservice.domain.dto.movie.MovieDataRequest;
import com.project.cinemaservice.domain.dto.movie.MovieEditRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFilters;
import com.project.cinemaservice.domain.dto.movie.MovieFiltersRequest;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetails;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsAdminResponse;
import com.project.cinemaservice.domain.dto.movie.MoviePageDetailsResponse;
import com.project.cinemaservice.persistence.model.Genre;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.MovieGenre;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface responsible for mapping between Movie entity and related DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieMapper {

  Movie toMovieEntity(MovieDataRequest movieDataRequest);

  @Mapping(source = "movie.auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "movie.auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "movie.auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "movie.auditEntity.modifiedBy", target = "modifiedBy")
  MovieAdminResponse toMovieAdminResponse(Movie movie, String previewUrl, String trailerUrl);

  MovieClientResponse toMovieClientResponse(Movie movie, String previewUrl, String trailerUrl);

  void updateEntity(@MappingTarget Movie movie, MovieEditRequest movieEditRequest);

  @Mapping(target = "genres", source = "genres")
  MovieFilters toMovieFilters(
      MovieFiltersRequest movieFiltersRequest, List<Genre> genres);

  MoviePageDetailsResponse toMoviePageDetailsResponse(MoviePageDetails moviePageDetails,
      String previewUrl);

  @BeanMapping(builder = @Builder(disableBuilder = true))
  MoviePageDetailsAdminResponse toMoviePageDetailsAdminResponse(MoviePageDetails moviePageDetails,
      String previewUrl);

  MovieBriefInfo toMovieBriefInfo(Movie movie);

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
