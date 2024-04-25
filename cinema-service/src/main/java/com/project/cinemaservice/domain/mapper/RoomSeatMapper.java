package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatAdminResponse;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatCreateRequest;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.model.RoomSeat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for mapping between RoomSeat entity and related DTOs.
 */
@Mapper(componentModel = "spring")
public interface RoomSeatMapper {

  @Mapping(source = "cinemaRoom", target = "cinemaRoom")
  RoomSeat toRoomSeatEntity(RoomSeatCreateRequest roomSeatCreateRequest, CinemaRoom cinemaRoom);

  @Mapping(source = "cinemaRoom.id", target = "cinemaRoomId")
  RoomSeatAdminResponse toRoomSeatAdminResponse(RoomSeat roomSeat);
}
