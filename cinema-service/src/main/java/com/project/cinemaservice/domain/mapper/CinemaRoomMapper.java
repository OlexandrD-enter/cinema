package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomAdminResponse;
import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomCreateRequest;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.model.RoomSeat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for mapping between CinemaRoom entity and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface CinemaRoomMapper {

  @Mapping(source = "cinemaRoomCreateRequest.name", target = "name")
  @Mapping(source = "cinemaRoomCreateRequest.roomType", target = "roomType")
  @Mapping(source = "cinema", target = "cinema")
  CinemaRoom toCinemaRoomEntity(CinemaRoomCreateRequest cinemaRoomCreateRequest, Cinema cinema);

  @Mapping(source = "auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "auditEntity.modifiedBy", target = "modifiedBy")
  @Mapping(source = "cinema.id", target = "cinemaId")
  @Mapping(target = "roomSeats", expression = "java(mapRoomSeats(cinemaRoom.getRoomSeats()))")
  CinemaRoomAdminResponse toCinemaRoomAdminResponse(CinemaRoom cinemaRoom);

  /**
   * Maps a list of RoomSeat entities to a set of RoomSeatBriefInfo DTOs.
   *
   * @param roomSeats The set of RoomSeat entities to be mapped.
   * @return The mapped set of RoomSeatBriefInfo DTOs.
   */
  default List<RoomSeatBriefInfo> mapRoomSeats(List<RoomSeat> roomSeats) {
    return roomSeats.stream()
        .map(this::toRoomSeatBriefInfo)
        .toList();
  }

  RoomSeatBriefInfo toRoomSeatBriefInfo(RoomSeat roomSeat);
}
