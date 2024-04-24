package com.project.cinemaservice.domain.dto.cinemaroom;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a cinema room response for admin.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CinemaRoomAdminResponse {

  private Long id;
  private String name;
  private String roomType;
  private Long cinemaId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String modifiedBy;
}
