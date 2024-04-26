package com.project.cinemaservice.domain.dto.cinema;

import com.project.cinemaservice.domain.dto.cinemaroom.CinemaRoomBriefInfo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a cinema response for admin.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CinemaAdminResponse {

  private Long id;
  private String name;
  private String city;
  private String streetAddress;
  private List<CinemaRoomBriefInfo> cinemaRooms;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
