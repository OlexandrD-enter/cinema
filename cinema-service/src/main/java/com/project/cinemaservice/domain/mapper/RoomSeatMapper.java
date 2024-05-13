package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatAdminResponse;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatCreateRequest;
import com.project.cinemaservice.persistence.model.RoomSeat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface responsible for mapping between RoomSeat entity and related DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomSeatMapper {

  RoomSeat toRoomSeatEntity(RoomSeatCreateRequest roomSeatCreateRequest);

  @Mapping(source = "cinemaRoom.id", target = "cinemaRoomId")
  RoomSeatAdminResponse toRoomSeatAdminResponse(RoomSeat roomSeat);
}
