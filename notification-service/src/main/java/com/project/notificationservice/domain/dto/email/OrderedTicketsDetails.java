package com.project.notificationservice.domain.dto.email;

import com.project.notificationservice.domain.dto.order.OrderedTickets;
import lombok.Getter;

/**
 * A specific extension of EmailDetails representing details for a ordered tickets email.
 */
@Getter
public class OrderedTicketsDetails extends EmailDetails {

  private final OrderedTickets orderedTickets;

  /**
   * Constructs a new OrderPaidTicketsDetails instance with the specified details.
   *
   * @param userEmail        The user email recipient.
   * @param orderedTickets The order details.
   */
  public OrderedTicketsDetails(String userEmail, OrderedTickets orderedTickets) {
    super(userEmail, EmailType.ORDERED_TICKETS_EMAIL);
    this.orderedTickets = orderedTickets;
  }
}