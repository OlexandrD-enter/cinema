package com.project.cinemaservice.domain.dto.showtime;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a showtime admin response.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeAdminResponse {

  private Long id;
  private Long movieId;
  private Long cinemaRoomId;
  private BigDecimal price;
  private List<RoomSeatBriefInfo> allRoomSeats;
  private List<RoomSeatBriefInfo> bookedRoomSeats;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
