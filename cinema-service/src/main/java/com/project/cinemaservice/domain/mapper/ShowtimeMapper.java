package com.project.cinemaservice.domain.mapper;

import com.project.cinemaservice.domain.dto.showtime.ShowtimeAdminResponse;
import com.project.cinemaservice.persistence.model.Showtime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShowtimeMapper {

  @Mapping(source = "movie.id", target = "movieId")
  @Mapping(source = "cinemaRoom.id", target = "cinemaRoomId")
  @Mapping(source = "auditEntity.createdAt", target = "createdAt")
  @Mapping(source = "auditEntity.updatedAt", target = "updatedAt")
  @Mapping(source = "auditEntity.createdBy", target = "createdBy")
  @Mapping(source = "auditEntity.modifiedBy", target = "modifiedBy")
  ShowtimeAdminResponse toShowtimeAdminResponse(Showtime showtime);

}
