package com.project.cinemaservice.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents the order paid tickets email data for a user.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderedTicketsEvent {

  private Long orderId;
  private String ownerEmail;
  private String movieName;
  private String moviePreviewUrl;
  private Integer ageLimit;
  private LocalDateTime startDate;
  private String cinemaRoomName;
  private String city;
  private String streetAddress;
  private List<TicketDetails> tickets;

  /**
   * Represents the tickets details.
   */
  @Getter
  @Setter
  @RequiredArgsConstructor
  @AllArgsConstructor
  public static class TicketDetails {

    private String cinemaRoomName;
    private Long roomSeatNumber;
    private BigDecimal price;
  }
}
