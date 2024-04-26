package com.project.cinemaservice.domain.dto.cinemaroom;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a cinema room response for admin.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaRoomAdminResponse {

  private Long id;
  private String name;
  private String roomType;
  private Long cinemaId;
  private List<RoomSeatBriefInfo> roomSeats;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
