package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.domain.dto.order.OrderCreateRequest;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.domain.mapper.OrderMapper;
import com.project.cinemaservice.messaging.OrderReservationEventPublisher;
import com.project.cinemaservice.persistence.model.Order;
import com.project.cinemaservice.persistence.model.RoomSeat;
import com.project.cinemaservice.persistence.model.Showtime;
import com.project.cinemaservice.persistence.model.Ticket;
import com.project.cinemaservice.persistence.repository.OrderRepository;
import com.project.cinemaservice.persistence.repository.OrderTicketRepository;
import com.project.cinemaservice.persistence.repository.RoomSeatRepository;
import com.project.cinemaservice.persistence.repository.ShowtimeRepository;
import com.project.cinemaservice.persistence.repository.TicketRepository;
import com.project.cinemaservice.service.exception.RoomSeatAlreadyBookedException;
import com.project.cinemaservice.service.exception.ShowtimeAlreadyStartedException;
import com.project.cinemaservice.service.impl.OrderServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderTicketRepository orderTicketRepository;

  @Mock
  private RoomSeatRepository roomSeatRepository;

  @Mock
  private ShowtimeRepository showtimeRepository;

  @Mock
  private TicketRepository ticketRepository;

  @Mock
  private OrderReservationEventPublisher orderReservationEventPublisher;

  @Mock
  private OrderMapper orderMapper;

  @InjectMocks
  private OrderServiceImpl orderService;

  @Test
  void createOrder_Success() {
    // Given
    OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
    orderCreateRequest.setShowTimeId(1L);
    List<Long> selectedRoomSeatsIds = new ArrayList<>();
    selectedRoomSeatsIds.add(1L);
    selectedRoomSeatsIds.add(2L);
    orderCreateRequest.setSelectedRoomSeatsIds(selectedRoomSeatsIds);
    Showtime showtime = new Showtime();
    showtime.setId(1L);

    Order order = new Order();
    order.setId(1L);

    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
    when(roomSeatRepository.findById(1L)).thenReturn(Optional.of(new RoomSeat()));
    when(roomSeatRepository.findById(2L)).thenReturn(Optional.of(new RoomSeat()));
    when(ticketRepository.save(any())).thenReturn(new Ticket());
    when(orderMapper.toOrderClientResponse(order, showtime.getId(), selectedRoomSeatsIds))
        .thenReturn(new OrderClientResponse());

    // When
    OrderClientResponse response = orderService.createOrder(orderCreateRequest);

    // Then
    assertNotNull(response);
    verify(orderRepository, times(1)).save(any());
    verify(orderReservationEventPublisher, times(1)).sendOrderReservationEvent(any());
  }

  @Test
  void createOrder_ShowtimeNotFound_ThrowsEntityNotFoundException() {
    // Given
    OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
    orderCreateRequest.setShowTimeId(1L);
    when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(orderCreateRequest));
    verify(orderRepository, never()).save(any());
    verify(orderReservationEventPublisher, never()).sendOrderReservationEvent(any());
  }

  @Test
  void createOrder_RoomSeatNotFound_ThrowsEntityNotFoundException() {
    // Given
    OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
    orderCreateRequest.setShowTimeId(1L);
    List<Long> selectedRoomSeatsIds = new ArrayList<>();
    selectedRoomSeatsIds.add(1L);
    selectedRoomSeatsIds.add(2L);
    orderCreateRequest.setSelectedRoomSeatsIds(selectedRoomSeatsIds);
    Showtime showtime = new Showtime();
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
    when(roomSeatRepository.findById(1L)).thenReturn(Optional.of(new RoomSeat()));
    when(roomSeatRepository.findById(2L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(orderCreateRequest));
    verify(orderRepository, never()).save(any());
    verify(orderReservationEventPublisher, never()).sendOrderReservationEvent(any());
  }

  @Test
  void createOrder_WhenSeatsAlreadyBooked_ThrowsRoomSeatAlreadyBookedException() {
    // Given
    OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
    orderCreateRequest.setShowTimeId(1L);
    List<Long> selectedRoomSeatsIds = new ArrayList<>();
    selectedRoomSeatsIds.add(1L);
    selectedRoomSeatsIds.add(2L);
    orderCreateRequest.setSelectedRoomSeatsIds(selectedRoomSeatsIds);

    List<RoomSeatBriefInfo> alreadyBookedRoomSeats = new ArrayList<>(
        List.of(new RoomSeatBriefInfo(1L, 1L)));

    when(orderTicketRepository.findAllByTicketShowtimeAndOrderStatusReservedOrPaid(1L))
        .thenReturn(alreadyBookedRoomSeats);

    // When & Then
    assertThrows(RoomSeatAlreadyBookedException.class, () -> orderService.createOrder(orderCreateRequest));
    verify(orderRepository, never()).save(any());
    verify(orderReservationEventPublisher, never()).sendOrderReservationEvent(any());
  }

  @Test
  void createOrder_WhenShowtimePassed_ThrowsShowtimeAlreadyStartedException() {
    // Given
    OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
    orderCreateRequest.setShowTimeId(1L);
    Showtime showtime = new Showtime();
    showtime.setStartDate(LocalDateTime.now().minusHours(1));
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

    // When & Then
    assertThrows(ShowtimeAlreadyStartedException.class, () -> orderService.createOrder(orderCreateRequest));
    verify(orderRepository, never()).save(any());
    verify(orderReservationEventPublisher, never()).sendOrderReservationEvent(any());
  }
}
