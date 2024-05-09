package com.project.cinemaservice.service.impl;

import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.domain.dto.order.OrderCreateRequest;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.domain.mapper.OrderMapper;
import com.project.cinemaservice.messaging.OrderReservationEventPublisher;
import com.project.cinemaservice.messaging.event.OrderReservationEvent;
import com.project.cinemaservice.persistence.enums.OrderStatus;
import com.project.cinemaservice.persistence.model.Order;
import com.project.cinemaservice.persistence.model.OrderTicket;
import com.project.cinemaservice.persistence.model.RoomSeat;
import com.project.cinemaservice.persistence.model.Showtime;
import com.project.cinemaservice.persistence.model.Ticket;
import com.project.cinemaservice.persistence.repository.OrderRepository;
import com.project.cinemaservice.persistence.repository.OrderTicketRepository;
import com.project.cinemaservice.persistence.repository.RoomSeatRepository;
import com.project.cinemaservice.persistence.repository.ShowtimeRepository;
import com.project.cinemaservice.persistence.repository.TicketRepository;
import com.project.cinemaservice.service.OrderService;
import com.project.cinemaservice.service.exception.RoomSeatAlreadyBookedException;
import com.project.cinemaservice.service.exception.ShowtimeAlreadyStartedException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrderService implementation responsible for movies related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderTicketRepository orderTicketRepository;
  private final RoomSeatRepository roomSeatRepository;
  private final ShowtimeRepository showtimeRepository;
  private final TicketRepository ticketRepository;
  private final OrderReservationEventPublisher orderReservationEventPublisher;
  private final OrderMapper orderMapper;

  @Transactional
  @Override
  public OrderClientResponse createOrder(OrderCreateRequest orderCreateRequest) {
    Long showTimeId = orderCreateRequest.getShowTimeId();

    checkIfRoomSeatsAvailableForOrder(showTimeId, orderCreateRequest.getSelectedRoomSeatsIds());

    Order order = Order.builder()
        .orderStatus(OrderStatus.RESERVED)
        .build();

    Showtime showtime = showtimeRepository.findById(showTimeId).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("Showtime with id=%d not found", showTimeId)));

    if (LocalDateTime.now().isAfter(showtime.getStartDate())) {
      throw new ShowtimeAlreadyStartedException(
          String.format("Showtime with id=%d already passed", showTimeId));
    }

    List<OrderTicket> orderTickets = orderCreateRequest.getSelectedRoomSeatsIds().stream()
        .map(roomSeatRequest -> {
          RoomSeat roomSeat = roomSeatRepository.findById(roomSeatRequest).orElseThrow(
              () -> new EntityNotFoundException(
                  String.format("RoomSeat with id=%d not found", roomSeatRequest)));

          Ticket ticket = ticketRepository.save(Ticket.builder()
              .roomSeat(roomSeat)
              .showtime(showtime)
              .build());

          return OrderTicket.builder()
              .order(order)
              .ticket(ticket)
              .build();
        }).toList();

    order.setOrderTickets(orderTickets);

    Order savedOrder = orderRepository.save(order);

    orderReservationEventPublisher.sendOrderReservationEvent(
        new OrderReservationEvent(savedOrder.getId()));

    return orderMapper.toOrderClientResponse(savedOrder, showtime.getId(),
        orderCreateRequest.getSelectedRoomSeatsIds());
  }

  private void checkIfRoomSeatsAvailableForOrder(Long showtimeId, List<Long> roomSeatIds) {

    List<RoomSeatBriefInfo> bookedSeatsByShowtime =
        orderTicketRepository.findAllByTicketShowtimeAndOrderStatusReservedOrPaid(showtimeId);

    if (!bookedSeatsByShowtime.isEmpty()) {
      List<Long> conflictingSeatIds = bookedSeatsByShowtime.stream()
          .map(RoomSeatBriefInfo::getId)
          .filter(roomSeatIds::contains)
          .toList();

      if (!conflictingSeatIds.isEmpty()) {
        throw new RoomSeatAlreadyBookedException(
            "The following seats are already booked: " + conflictingSeatIds);
      }
    }
  }
}
