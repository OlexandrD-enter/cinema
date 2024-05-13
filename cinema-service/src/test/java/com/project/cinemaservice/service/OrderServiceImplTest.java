package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.project.cinemaservice.domain.dto.movie.MovieFileResponseUrl;
import com.project.cinemaservice.domain.dto.order.OrderBriefInfo;
import com.project.cinemaservice.domain.dto.order.OrderBriefInfoAdmin;
import com.project.cinemaservice.domain.dto.order.OrderClientDetails;
import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.domain.dto.order.OrderCreateRequest;
import com.project.cinemaservice.domain.dto.order.OrderDetails;
import com.project.cinemaservice.domain.dto.order.OrderFilterRequest;
import com.project.cinemaservice.domain.dto.order.OrderStatusDetails;
import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.domain.mapper.OrderMapper;
import com.project.cinemaservice.messaging.OrderReservationEventPublisher;
import com.project.cinemaservice.persistence.enums.OrderStatus;
import com.project.cinemaservice.persistence.model.AuditEntity;
import com.project.cinemaservice.persistence.model.Order;
import com.project.cinemaservice.persistence.model.OrderPaymentDetails;
import com.project.cinemaservice.persistence.model.RoomSeat;
import com.project.cinemaservice.persistence.model.Showtime;
import com.project.cinemaservice.persistence.model.Ticket;
import com.project.cinemaservice.persistence.repository.OrderPaymentDetailsRepository;
import com.project.cinemaservice.persistence.repository.OrderRepository;
import com.project.cinemaservice.persistence.repository.OrderTicketRepository;
import com.project.cinemaservice.persistence.repository.RoomSeatRepository;
import com.project.cinemaservice.persistence.repository.ShowtimeRepository;
import com.project.cinemaservice.persistence.repository.TicketRepository;
import com.project.cinemaservice.service.exception.CancellationTimeIsUpException;
import com.project.cinemaservice.service.exception.DateViolationException;
import com.project.cinemaservice.service.exception.ForbiddenOperationException;
import com.project.cinemaservice.service.exception.IllegalOrderStatusException;
import com.project.cinemaservice.service.exception.PriceViolationException;
import com.project.cinemaservice.service.exception.RoomSeatAlreadyBookedException;
import com.project.cinemaservice.service.exception.ShowtimeAlreadyStartedException;
import com.project.cinemaservice.service.impl.OrderServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
  private OrderPaymentDetailsRepository orderPaymentDetailsRepository;

  @Mock
  private Map<OrderStatus, Set<OrderStatus>> orderStatusTransitionsMap;

  @Mock
  private OrderReservationEventPublisher orderReservationEventPublisher;

  @Mock
  private OrderMapper orderMapper;

  @Mock
  private MediaServiceClient mediaServiceClient;

  @Mock
  private PaymentServiceClient paymentServiceClient;

  @InjectMocks
  private OrderServiceImpl orderService;

  @Test
  void createOrder_Success() {
    // Given
    OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
    orderCreateRequest.setShowTimeId(1L);
    List<Long> selectedRoomSeatsIds = new ArrayList<>();
    selectedRoomSeatsIds.add(1L);
    orderCreateRequest.setSelectedRoomSeatsIds(selectedRoomSeatsIds);
    Showtime showtime = new Showtime();
    showtime.setStartDate(LocalDateTime.now().plusHours(6));
    showtime.setId(1L);

    Order order = new Order();
    order.setId(1L);

    Ticket ticket = Ticket.builder()
        .showtime(showtime)
        .roomSeat(RoomSeat.builder()
            .seatNumber(1L)
            .build())
        .build();

    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
    when(roomSeatRepository.findById(1L)).thenReturn(Optional.of(new RoomSeat()));
    when(ticketRepository.save(any())).thenReturn(ticket);
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
    showtime.setStartDate(LocalDateTime.now().plusHours(6));
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
    assertThrows(RoomSeatAlreadyBookedException.class,
        () -> orderService.createOrder(orderCreateRequest));
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
    assertThrows(ShowtimeAlreadyStartedException.class,
        () -> orderService.createOrder(orderCreateRequest));
    verify(orderRepository, never()).save(any());
    verify(orderReservationEventPublisher, never()).sendOrderReservationEvent(any());
  }

  @Test
  void getOrderForClient_Success() {
    // Given
    OrderDetails orderDetails = new OrderDetails();
    orderDetails.setId(1L);
    orderDetails.setOrderStatus(OrderStatus.RESERVED);
    orderDetails.setUserEmail("demchenko@gmail.com");

    OrderClientDetails orderClientDetails = new OrderClientDetails();
    orderClientDetails.setId(orderDetails.getId());
    orderClientDetails.setOrderStatus(orderDetails.getOrderStatus());

    Collection<GrantedAuthority> authorities = Collections.singletonList(() -> "ROLE_ADMIN");
    Authentication authentication = new UsernamePasswordAuthenticationToken("demchenko@gmail.com",
        "password", authorities);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(orderRepository.findOrderDetails(1L)).thenReturn(Optional.of(orderDetails));
    when(mediaServiceClient.getFile(any())).thenReturn(new MovieFileResponseUrl());
    when(orderMapper.toOrderClientDetails(any(), anyList(), any())).thenReturn(orderClientDetails);

    // When
    OrderClientDetails result = orderService.getOrderForClient(1L);

    // Then
    assertNotNull(result);
    assertEquals(OrderStatus.RESERVED, result.getOrderStatus());
    assertEquals(1L, result.getId());
  }

  @Test
  void getOrderForClient_WhenUserNotOrderOwner_ThrowsForbiddenOperationException() {
    // Given
    OrderDetails orderDetails = new OrderDetails();
    orderDetails.setId(1L);
    orderDetails.setOrderStatus(OrderStatus.RESERVED);
    orderDetails.setUserEmail("demchenko@gmail.com");

    OrderClientDetails orderClientDetails = new OrderClientDetails();
    orderClientDetails.setId(orderDetails.getId());
    orderClientDetails.setOrderStatus(orderDetails.getOrderStatus());

    Collection<GrantedAuthority> authorities = Collections.singletonList(() -> "ROLE_USER");
    Authentication authentication = new UsernamePasswordAuthenticationToken("user@gmail.com",
        "password", authorities);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(orderRepository.findOrderDetails(1L)).thenReturn(Optional.of(orderDetails));

    // When & Then
    assertThrows(ForbiddenOperationException.class, () -> orderService.getOrderForClient(1L));
  }

  @Test
  void getOrderForClient_OrderNotFound_ThrowsEntityNotFoundException() {
    // Given
    when(orderRepository.findOrderDetails(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> orderService.getOrderForClient(1L));
  }

  @Test
  void confirmOrderPayment_Success() {
    // Given
    Order order = new Order();
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderMapper.toOrderStatusDetails(any())).thenReturn(new OrderStatusDetails());

    // When
    OrderStatusDetails result = orderService.confirmOrderPayment(1L, "transactionId");

    // Then
    assertNotNull(result);
    assertEquals(OrderStatus.PAID, order.getOrderStatus());
  }

  @Test
  void confirmOrderPayment_OrderNotFound_ThrowsEntityNotFoundException() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class,
        () -> orderService.confirmOrderPayment(1L, "transactionId"));
  }

  @Test
  void checkIfOrderPaid_Success() {
    // Given
    Order order = new Order();
    order.setOrderStatus(OrderStatus.PAID);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderMapper.toOrderStatusDetails(any())).thenReturn(new OrderStatusDetails());

    // When
    OrderStatusDetails result = orderService.checkIfOrderPaid(1L);

    // Then
    assertNotNull(result);
    assertEquals(OrderStatus.PAID, order.getOrderStatus());
  }

  @Test
  void checkIfOrderPaid_OrderStatusReserved_ShouldCancelOrder() {
    // Given
    Order order = new Order();
    order.setOrderStatus(OrderStatus.RESERVED);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderMapper.toOrderStatusDetails(any())).thenReturn(new OrderStatusDetails());

    // When
    OrderStatusDetails result = orderService.checkIfOrderPaid(1L);

    // Then
    assertNotNull(result);
    assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
  }

  @Test
  void checkIfOrderPaid_OrderNotFound_ThrowsEntityNotFoundException() {
    // Given
    when(orderRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> orderService.checkIfOrderPaid(1L));
  }

  @Test
  void cancelOrder_Success() {
    // Given
    Order order = new Order();
    order.setId(1L);
    AuditEntity auditEntity = new AuditEntity();
    auditEntity.setCreatedBy("demchenko@gmail.com");
    order.setOrderStatus(OrderStatus.RESERVED);
    order.setAuditEntity(auditEntity);
    OrderPaymentDetails orderPaymentDetails = new OrderPaymentDetails();
    orderPaymentDetails.setOrder(order);

    when(orderPaymentDetailsRepository.findByOrderId(1L)).thenReturn(
        Optional.of(orderPaymentDetails));
    when(orderStatusTransitionsMap.get(OrderStatus.CANCELLED)).thenReturn(
        Collections.singleton(OrderStatus.RESERVED));
    when(orderPaymentDetailsRepository.save(any())).thenReturn(orderPaymentDetails);
    when(showtimeRepository.findStartDateOfShowtimeByOrderId(1L)).thenReturn(
        LocalDateTime.now().plusHours(4));
    when(orderMapper.toOrderStatusDetails(any())).thenReturn(new OrderStatusDetails());

    Collection<GrantedAuthority> authorities = Collections.singletonList(() -> "ROLE_ADMIN");
    Authentication authentication = new UsernamePasswordAuthenticationToken("demchenko@gmail.com",
        "password", authorities);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // When
    OrderStatusDetails result = orderService.cancelOrder(1L);

    // Then
    assertNotNull(result);
    assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
  }

  @Test
  void cancelOrder_WhenStatusNotAllowed_ThrowsIllegalOrderStatusException() {
    // Given
    Order order = new Order();
    order.setOrderStatus(OrderStatus.REFUNDED);
    OrderPaymentDetails orderPaymentDetails = new OrderPaymentDetails();
    orderPaymentDetails.setOrder(order);

    when(orderPaymentDetailsRepository.findByOrderId(1L)).thenReturn(
        Optional.of(orderPaymentDetails));
    when(orderStatusTransitionsMap.get(OrderStatus.CANCELLED)).thenReturn(
        Collections.singleton(OrderStatus.RESERVED));

    // When & Then
    assertThrows(IllegalOrderStatusException.class, () -> orderService.cancelOrder(1L));
  }

  @Test
  void cancelOrder_OrderNotFound_ThrowsEntityNotFoundException() {
    // Given
    when(orderPaymentDetailsRepository.findByOrderId(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> orderService.cancelOrder(1L));
  }

  @Test
  void cancelOrder_CancellationTimeToLate_ThrowsCancellationTimeIsUpException() {
    // Given
    Order order = new Order();
    order.setId(1L);
    AuditEntity auditEntity = new AuditEntity();
    auditEntity.setCreatedBy("demchenko@gmail.com");
    order.setOrderStatus(OrderStatus.RESERVED);
    order.setAuditEntity(auditEntity);
    OrderPaymentDetails orderPaymentDetails = new OrderPaymentDetails();
    orderPaymentDetails.setOrder(order);

    when(orderPaymentDetailsRepository.findByOrderId(1L)).thenReturn(
        Optional.of(orderPaymentDetails));
    when(orderStatusTransitionsMap.get(OrderStatus.CANCELLED)).thenReturn(
        Collections.singleton(OrderStatus.RESERVED));
    when(showtimeRepository.findStartDateOfShowtimeByOrderId(1L)).thenReturn(LocalDateTime.now());

    Collection<GrantedAuthority> authorities = Collections.singletonList(() -> "ROLE_ADMIN");
    Authentication authentication = new UsernamePasswordAuthenticationToken("demchenko@gmail.com",
        "password", authorities);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // When & Then
    assertThrows(CancellationTimeIsUpException.class, () -> orderService.cancelOrder(1L));
  }

  @Test
  void getAllOrdersForClient_Success() {
    // Given
    Pageable pageable = Pageable.unpaged();
    List<OrderBriefInfo> orderBriefInfos = new ArrayList<>();
    orderBriefInfos.add(new OrderBriefInfo(1L, LocalDateTime.now(), OrderStatus.RESERVED,
        "Cinema Name", "Cinema City", "Cinema Address", "Movie Name",
        LocalDateTime.now(), BigDecimal.valueOf(100.00)));
    Page<OrderBriefInfo> expectedPage = new PageImpl<>(orderBriefInfos);

    when(orderRepository.findAllUserOrders(pageable, "testUser")).thenReturn(expectedPage);
    Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", null);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    // When
    Page<OrderBriefInfo> resultPage = orderService.getAllOrdersForClient(pageable);

    // Then
    assertEquals(expectedPage, resultPage);
    verify(orderRepository, times(1)).findAllUserOrders(pageable, "testUser");
  }

  @Test
  void getAllOrdersByFilter_Success() {
    // Given
    Pageable pageable = Pageable.unpaged();
    List<OrderBriefInfo> orderBriefInfos = new ArrayList<>();
    orderBriefInfos.add(new OrderBriefInfo(1L, LocalDateTime.now(), OrderStatus.RESERVED,
        "Cinema Name", "Cinema City", "Cinema Address", "Movie Name",
        LocalDateTime.now(), BigDecimal.valueOf(100.00)));
    Page<OrderBriefInfo> expectedPage = new PageImpl<>(orderBriefInfos);

    LocalDateTime minCreationDate = LocalDateTime.of(2022, 1, 1, 0, 0);
    LocalDateTime maxCreationDate = LocalDateTime.of(2022, 12, 31, 23, 59);
    BigDecimal minTotalSum = BigDecimal.valueOf(50.00);
    BigDecimal maxTotalSum = BigDecimal.valueOf(200.00);

    OrderBriefInfoAdmin expectedAdminInfo = new OrderBriefInfoAdmin(expectedPage,
        minCreationDate, maxCreationDate, minTotalSum, maxTotalSum);

    OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
    orderFilterRequest.setFromOrderCreationTime(minCreationDate);
    orderFilterRequest.setToOrderCreationTime(maxCreationDate);
    orderFilterRequest.setFromPrice(minTotalSum);
    orderFilterRequest.setToPrice(maxTotalSum);

    when(orderRepository.findAllOrders(pageable, orderFilterRequest)).thenReturn(expectedAdminInfo);

    // When
    OrderBriefInfoAdmin resultAdminInfo = orderService.getAllOrdersByFilter(pageable, orderFilterRequest);

    // Then
    assertEquals(expectedAdminInfo, resultAdminInfo);
    verify(orderRepository, times(1)).findAllOrders(pageable, orderFilterRequest);
  }

  @Test
  void getAllOrdersByFilter_InvalidPriceRange_ThrowsPriceViolationException() {
    // Given
    OrderFilterRequest invalidFilterRequest = new OrderFilterRequest();
    invalidFilterRequest.setFromPrice(BigDecimal.valueOf(100));
    invalidFilterRequest.setToPrice(BigDecimal.valueOf(50));

    Pageable pageable = Pageable.unpaged();

    // When & Then
    assertThrows(PriceViolationException.class, () -> {
      orderService.getAllOrdersByFilter(pageable, invalidFilterRequest);
    });
    verifyNoInteractions(orderRepository);
  }

  @Test
  void getAllOrdersByFilter_InvalidDateRange_ThrowsDateViolationException() {
    // Given
    OrderFilterRequest invalidFilterRequest = new OrderFilterRequest();
    invalidFilterRequest.setFromOrderCreationTime(LocalDateTime.of(2022, 1, 1, 0, 0));
    invalidFilterRequest.setToOrderCreationTime(LocalDateTime.of(2021, 12, 31, 23, 59));

    Pageable pageable = Pageable.unpaged();

    // When & Then
    assertThrows(DateViolationException.class, () -> {
      orderService.getAllOrdersByFilter(pageable, invalidFilterRequest);
    });
    verifyNoInteractions(orderRepository);
  }
}
