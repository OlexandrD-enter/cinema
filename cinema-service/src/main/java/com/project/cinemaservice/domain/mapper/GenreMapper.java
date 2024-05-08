package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.genre.GenreAdminBriefResponse;
import com.project.cinemaservice.domain.dto.genre.GenreAdminResponse;
import com.project.cinemaservice.domain.dto.genre.GenreDataRequest;
import com.project.cinemaservice.persistence.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface responsible for mapping between Genre entity and related DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenreMapper {

  Genre toGenreEntity(GenreDataRequest genreDataRequest);

  @Mapping(source = "auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "auditEntity.modifiedBy", target = "modifiedBy")
  GenreAdminResponse toGenreAdminResponse(Genre genre);

  GenreAdminBriefResponse toGenreAdminBriefResponse(Genre genre);
}
