package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeAdminResponse;
import com.project.cinemaservice.domain.dto.showtime.ShowtimeClientResponse;
import com.project.cinemaservice.persistence.model.Showtime;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface responsible for mapping between Showtime entity and related DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShowtimeMapper {

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "movie.id", target = "movieId")
  @Mapping(source = "cinemaRoom.id", target = "cinemaRoomId")
  @Mapping(source = "auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "auditEntity.modifiedBy", target = "modifiedBy")
  ShowtimeAdminResponse toShowtimeAdminResponse(Showtime showtime);

  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "showtime.movie.id", target = "movieId")
  @Mapping(source = "showtime.cinemaRoom.id", target = "cinemaRoomId")
  @Mapping(source = "showtime.auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "showtime.auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "showtime.auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "showtime.auditEntity.modifiedBy", target = "modifiedBy")
  @Mapping(source = "showtime", target = "allRoomSeats", qualifiedByName = "toAllRoomSeatBriefInfo")
  ShowtimeAdminResponse toShowtimeAdminResponse(Showtime showtime,
      List<RoomSeatBriefInfo> bookedRoomSeats);

  @Mapping(source = "showtime.movie.id", target = "movieId")
  @Mapping(source = "showtime.cinemaRoom.id", target = "cinemaRoomId")
  @Mapping(source = "showtime", target = "allRoomSeats", qualifiedByName = "toAllRoomSeatBriefInfo")
  ShowtimeClientResponse toShowtimeClientResponse(Showtime showtime,
      List<RoomSeatBriefInfo> bookedRoomSeats);

  /**
   * Maps all room seats of a Showtime to RoomSeatBriefInfo DTOs.
   *
   * @param showtime The Showtime entity containing room seats
   * @return List of RoomSeatBriefInfo DTOs
   */
  @Named("toAllRoomSeatBriefInfo")
  default List<RoomSeatBriefInfo> mapAllRoomSeats(Showtime showtime) {
    return showtime.getCinemaRoom().getRoomSeats().stream()
        .map(roomSeat -> new RoomSeatBriefInfo(roomSeat.getId(), roomSeat.getSeatNumber()))
        .toList();
  }
}
