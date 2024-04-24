package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.cinema.CinemaAdminResponse;
import com.project.cinemaservice.domain.dto.cinema.CinemaDataRequest;
import com.project.cinemaservice.persistence.model.Cinema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for mapping between Cinema entity and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface CinemaMapper {

  @Mapping(source = "auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "auditEntity.modifiedBy", target = "modifiedBy")
  CinemaAdminResponse toCinemaAdminResponse(Cinema cinema);

  Cinema toCinemaEntity(CinemaDataRequest cinemaDataRequest);

}
