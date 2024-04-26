package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.cinema.CinemaAdminResponse;
import com.project.cinemaservice.domain.dto.cinema.CinemaDataRequest;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomBriefInfo;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
  @Mapping(target = "cinemaRooms", expression = "java(mapCinemaRooms(cinema.getCinemaRooms()))")
  CinemaAdminResponse toCinemaAdminResponse(Cinema cinema);

  /**
   * Maps a list of CinemaRoom entities to a set of CinemaRoomBriefInfo DTOs.
   *
   * @param cinemaRooms The set of CinemaRoom entities to be mapped.
   * @return The mapped set of CinemaRoomBriefInfo DTOs.
   */
  default List<CinemaRoomBriefInfo> mapCinemaRooms(List<CinemaRoom> cinemaRooms) {
    return cinemaRooms.stream()
        .map(this::toCinemaRoomBriefInfo)
        .toList();
  }

  CinemaRoomBriefInfo toCinemaRoomBriefInfo(CinemaRoom cinemaRoom);

  Cinema toCinemaEntity(CinemaDataRequest cinemaDataRequest);

}
