package com.project.cinemaservice.service.impl;

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
import com.project.cinemaservice.messaging.OrderedTicketsEventPublisher;
import com.project.cinemaservice.messaging.event.OrderReservationEvent;
import com.project.cinemaservice.messaging.event.OrderedTicketsEvent;
import com.project.cinemaservice.messaging.event.OrderedTicketsEvent.TicketDetails;
import com.project.cinemaservice.persistence.enums.MovieFileType;
import com.project.cinemaservice.persistence.enums.OrderStatus;
import com.project.cinemaservice.persistence.model.Cinema;
import com.project.cinemaservice.persistence.model.CinemaRoom;
import com.project.cinemaservice.persistence.model.Movie;
import com.project.cinemaservice.persistence.model.MovieFile;
import com.project.cinemaservice.persistence.model.Order;
import com.project.cinemaservice.persistence.model.OrderPaymentDetails;
import com.project.cinemaservice.persistence.model.OrderTicket;
import com.project.cinemaservice.persistence.model.RoomSeat;
import com.project.cinemaservice.persistence.model.Showtime;
import com.project.cinemaservice.persistence.model.Ticket;
import com.project.cinemaservice.persistence.repository.OrderPaymentDetailsRepository;
import com.project.cinemaservice.persistence.repository.OrderRepository;
import com.project.cinemaservice.persistence.repository.OrderTicketRepository;
import com.project.cinemaservice.persistence.repository.RoomSeatRepository;
import com.project.cinemaservice.persistence.repository.ShowtimeRepository;
import com.project.cinemaservice.persistence.repository.TicketRepository;
import com.project.cinemaservice.service.MediaServiceClient;
import com.project.cinemaservice.service.OrderService;
import com.project.cinemaservice.service.PaymentServiceClient;
import com.project.cinemaservice.service.exception.CancellationTimeIsUpException;
import com.project.cinemaservice.service.exception.DateViolationException;
import com.project.cinemaservice.service.exception.ForbiddenOperationException;
import com.project.cinemaservice.service.exception.IllegalOrderStatusException;
import com.project.cinemaservice.service.exception.PriceViolationException;
import com.project.cinemaservice.service.exception.RoomSeatAlreadyBookedException;
import com.project.cinemaservice.service.exception.ShowtimeAlreadyStartedException;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrderService implementation responsible for movies related operations.
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderTicketRepository orderTicketRepository;
  private final RoomSeatRepository roomSeatRepository;
  private final ShowtimeRepository showtimeRepository;
  private final TicketRepository ticketRepository;
  private final OrderPaymentDetailsRepository orderPaymentDetailsRepository;
  private final Map<OrderStatus, Set<OrderStatus>> orderStatusTransitionsMap;
  private final OrderReservationEventPublisher orderReservationEventPublisher;
  private final OrderedTicketsEventPublisher orderedTicketsEventPublisher;
  private final OrderMapper orderMapper;
  private final MediaServiceClient mediaServiceClient;
  private final PaymentServiceClient paymentServiceClient;
  private final long leftTimeBeforeSuccessCancelOrOrderInMinutes;

  /**
   * Constructs an OrderServiceImpl with required repositories, mappers, clients, and configuration
   * parameters.
   *
   * @param orderRepository                             Repository for managing orders.
   * @param orderTicketRepository                       Repository for managing order tickets.
   * @param roomSeatRepository                          Repository for managing room seats.
   * @param showtimeRepository                          Repository for managing showtimes.
   * @param ticketRepository                            Repository for managing tickets.
   * @param orderPaymentDetailsRepository               Repository for managing order payment
   *                                                    details.
   * @param orderStatusTransitionsMap                   Map defining valid order status
   *                                                    transitions.
   * @param orderReservationEventPublisher              Event publisher for order reservation
   *                                                    events.
   * @param orderMapper                                 Mapper for converting order entities.
   * @param mediaServiceClient                          Client for interacting with media services.
   * @param paymentServiceClient                        Client for interacting with payment
   *                                                    services.
   * @param leftTimeBeforeSuccessCancelOrOrderInMinutes Time limit for successful order cancellation
   *                                                    or completion.
   */
  public OrderServiceImpl(OrderRepository orderRepository,
      OrderTicketRepository orderTicketRepository,
      RoomSeatRepository roomSeatRepository,
      ShowtimeRepository showtimeRepository,
      TicketRepository ticketRepository,
      OrderPaymentDetailsRepository orderPaymentDetailsRepository,
      Map<OrderStatus, Set<OrderStatus>> orderStatusTransitionsMap,
      OrderReservationEventPublisher orderReservationEventPublisher,
      OrderedTicketsEventPublisher orderedTicketsEventPublisher,
      OrderMapper orderMapper, MediaServiceClient mediaServiceClient,
      PaymentServiceClient paymentServiceClient,
      @Value("${orders.left-time-for-cancel}") long leftTimeBeforeSuccessCancelOrOrderInMinutes) {
    this.orderRepository = orderRepository;
    this.orderTicketRepository = orderTicketRepository;
    this.roomSeatRepository = roomSeatRepository;
    this.showtimeRepository = showtimeRepository;
    this.ticketRepository = ticketRepository;
    this.orderPaymentDetailsRepository = orderPaymentDetailsRepository;
    this.orderStatusTransitionsMap = orderStatusTransitionsMap;
    this.orderReservationEventPublisher = orderReservationEventPublisher;
    this.orderedTicketsEventPublisher = orderedTicketsEventPublisher;
    this.orderMapper = orderMapper;
    this.mediaServiceClient = mediaServiceClient;
    this.paymentServiceClient = paymentServiceClient;
    this.leftTimeBeforeSuccessCancelOrOrderInMinutes = leftTimeBeforeSuccessCancelOrOrderInMinutes;
  }

  @Transactional
  @Override
  public OrderClientResponse createOrder(OrderCreateRequest orderCreateRequest) {
    Long showTimeId = orderCreateRequest.getShowTimeId();
    List<Long> selectedRoomSeatsIds = orderCreateRequest.getSelectedRoomSeatsIds();

    log.debug("Creating Order for showtime {} and roomSeats {}", showtimeRepository,
        selectedRoomSeatsIds);

    checkIfRoomSeatsAvailableForOrder(showTimeId, selectedRoomSeatsIds);

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

    Long roomIdFromShowtime = showtime.getCinemaRoom().getId();

    List<OrderTicket> orderTickets = orderCreateRequest.getSelectedRoomSeatsIds().stream()
        .map(roomSeatId -> {
          RoomSeat roomSeat = roomSeatRepository.findByIdAndCinemaRoomId(roomSeatId,
              roomIdFromShowtime).orElseThrow(() -> new EntityNotFoundException(
              String.format("RoomSeat with id=%d not found in CinemaRoom id=%d", roomSeatId,
                  showtime.getCinemaRoom().getId())));

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

    List<Long> bookedRoomSeatNumbers = orderTickets.stream()
        .map(OrderTicket::getTicket)
        .map(Ticket::getRoomSeat)
        .map(RoomSeat::getSeatNumber)
        .toList();

    Order savedOrder = orderRepository.save(order);

    orderReservationEventPublisher.sendOrderReservationEvent(
        new OrderReservationEvent(savedOrder.getId()));

    log.debug("Order created for showtime {} and roomSeats {}", showtimeRepository,
        selectedRoomSeatsIds);

    return orderMapper.toOrderClientResponse(savedOrder, showtime.getId(), bookedRoomSeatNumbers);
  }

  @Transactional
  @Override
  public OrderClientDetails getOrderForClient(Long orderId) {
    OrderDetails orderDetails = orderRepository.findOrderDetails(orderId).orElseThrow(
        () -> new EntityNotFoundException(
            String.format("Order with id=%d not found", orderId)));

    checkIfUserOrderOwnerOrUserAdmin(orderDetails.getUserEmail());

    List<Long> bookedSeatNumberIds = orderRepository.findBookedRoomSeatNumbers(orderId);
    MovieFileResponseUrl file = mediaServiceClient.getFile(orderDetails.getMoviePreviewFileId());

    return orderMapper.toOrderClientDetails(orderDetails, bookedSeatNumberIds, file.getAccessUrl());
  }

  @Transactional
  @Override
  public OrderStatusDetails confirmOrderPayment(Long orderId, String transactionId) {
    log.debug("Changing order status to paid for order {}", orderId);

    Order order = orderRepository.findById(orderId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Order with id=%d not found", orderId)));
    order.setOrderStatus(OrderStatus.PAID);

    orderPaymentDetailsRepository.save(OrderPaymentDetails.builder()
        .order(order)
        .transactionId(transactionId)
        .build());

    Order savedOrder = orderRepository.save(order);

    OrderedTicketsEvent orderedTickets = createOrderedTicketsEvent(order);

    orderedTicketsEventPublisher.sendOrderedTicketsEvent(orderedTickets);

    log.debug("Changed order status to paid for order {}", orderId);

    return orderMapper.toOrderStatusDetails(savedOrder);
  }

  @Transactional
  @Override
  public OrderStatusDetails checkIfOrderPaid(Long orderId) {
    log.debug("Checking if order status equals paid for order {}", orderId);

    Order order = orderRepository.findById(orderId).orElseThrow(
        () -> new EntityNotFoundException(String.format("Order with id=%d not found", orderId)));

    if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
      order.setOrderStatus(OrderStatus.CANCELLED);
    }
    Order savedOrder = orderRepository.save(order);

    log.debug("Changed order status to cancelled for order {}", orderId);

    return orderMapper.toOrderStatusDetails(savedOrder);
  }

  @Transactional
  @Override
  public OrderStatusDetails cancelOrder(Long orderId) {
    log.debug("Cancelling order {}", orderId);

    OrderPaymentDetails orderPaymentDetails =
        orderPaymentDetailsRepository.findByOrderId(orderId).orElseThrow(
            () -> new EntityNotFoundException(
                String.format("Order with id=%d not found", orderId)));

    Order order = orderPaymentDetails.getOrder();

    checkIfOrderStatusTransitionIsAllowed(order, OrderStatus.CANCELLED);

    checkIfUserOrderOwnerOrUserAdmin(order.getAuditEntity().getCreatedBy());

    checkIfCancellationTimeIsCorrect(order);

    OrderStatus orderStatus = order.getOrderStatus();
    if (orderStatus.equals(OrderStatus.RESERVED)) {
      orderPaymentDetails.getOrder().setOrderStatus(OrderStatus.CANCELLED);
    } else if (orderStatus.equals(OrderStatus.PAID)) {
      paymentServiceClient.refundPayment(orderPaymentDetails.getTransactionId());
      orderPaymentDetails.getOrder().setOrderStatus(OrderStatus.REFUNDED);
    }

    OrderPaymentDetails paymentDetails = orderPaymentDetailsRepository.save(orderPaymentDetails);

    log.debug("Order {} changed status to {}", paymentDetails.getOrder().getId(),
        paymentDetails.getOrder().getOrderStatus());

    return orderMapper.toOrderStatusDetails(paymentDetails.getOrder());
  }

  @Transactional
  @Override
  public Page<OrderBriefInfo> getAllOrdersForClient(Pageable pageable) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return orderRepository.findAllUserOrders(pageable, authentication.getName());
  }

  @Transactional
  @Override
  public OrderBriefInfoAdmin getAllOrdersByFilter(Pageable pageable,
      OrderFilterRequest orderFilterRequest) {
    validateFilterRequestData(orderFilterRequest);

    return orderRepository.findAllOrders(pageable, orderFilterRequest);
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

  private void checkIfUserOrderOwnerOrUserAdmin(String userOrderOwnerEmail) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    String authorityToCheck = "ROLE_ADMIN";

    boolean hasAdminAuthority = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(authority -> authority.equals(authorityToCheck));

    if (!userOrderOwnerEmail.equals(authentication.getName()) && !hasAdminAuthority) {
      throw new ForbiddenOperationException("User is not the owner of the order");
    }
  }

  private void checkIfOrderStatusTransitionIsAllowed(Order order, OrderStatus status) {
    Set<OrderStatus> allowedStatuses = orderStatusTransitionsMap.get(status);
    if (!allowedStatuses.contains(order.getOrderStatus())) {
      throw new IllegalOrderStatusException(String.format("Invalid order status for %s "
          + "Allowed statuses: %s", status, allowedStatuses));
    }
  }

  private void checkIfCancellationTimeIsCorrect(Order order) {
    LocalDateTime startDateOfShowtimeByOrderId =
        showtimeRepository.findStartDateOfShowtimeByOrderId(order.getId());
    if (LocalDateTime.now().plusMinutes(leftTimeBeforeSuccessCancelOrOrderInMinutes)
        .isAfter(startDateOfShowtimeByOrderId)) {
      throw new CancellationTimeIsUpException(
          String.format("It`s to late to cancel order with id=%d", order.getId()));
    }
  }

  private void validateFilterRequestData(OrderFilterRequest orderFilterRequest) {
    validatePriceRangeFromFilterRequest(orderFilterRequest);
    validateDateRangeFromFilterRequest(orderFilterRequest);
  }

  private void validatePriceRangeFromFilterRequest(OrderFilterRequest orderFilterRequest) {
    BigDecimal fromPrice = orderFilterRequest.getFromPrice();
    BigDecimal toPrice = orderFilterRequest.getToPrice();

    if (fromPrice != null && toPrice != null && fromPrice.compareTo(toPrice) > 0) {
      throw new PriceViolationException("'From price' should be lower than 'To price'");
    } else if ((fromPrice == null && toPrice != null) || (fromPrice != null && toPrice == null)) {
      throw new PriceViolationException("Both 'From price' and 'To price' must be provided");
    }
  }

  private void validateDateRangeFromFilterRequest(OrderFilterRequest orderFilterRequest) {
    LocalDateTime fromCreationTime = orderFilterRequest.getFromOrderCreationTime();
    LocalDateTime toCreationTime = orderFilterRequest.getToOrderCreationTime();

    if (fromCreationTime != null && toCreationTime != null
        && fromCreationTime.isAfter(toCreationTime)) {
      throw new DateViolationException(
          "'From creation time' should be lower than 'To creation time'");
    } else if ((fromCreationTime == null && toCreationTime != null) || (fromCreationTime != null
        && toCreationTime == null)) {
      throw new DateViolationException(
          "Both 'From creation time' and 'To creation time' must be provided");
    }
  }

  private OrderedTicketsEvent createOrderedTicketsEvent(Order order) {
    OrderedTicketsEvent orderedTickets = new OrderedTicketsEvent();
    order.getOrderTickets().stream()
        .map(OrderTicket::getTicket)
        .findFirst()
        .map(Ticket::getShowtime)
        .ifPresent(showtime -> {
          CinemaRoom cinemaRoom = showtime.getCinemaRoom();
          Cinema cinema = cinemaRoom.getCinema();
          Movie movie = showtime.getMovie();

          Long fileId = movie.getMovieFiles().stream()
              .filter(movieFile -> movieFile.getMovieFileType().equals(MovieFileType.MOVIE_PREVIEW))
              .findFirst()
              .map(MovieFile::getFileId)
              .orElseThrow(() -> new EntityNotFoundException("Movie preview file not found"));

          MovieFileResponseUrl file = mediaServiceClient.getFile(fileId);

          orderedTickets.setMovieName(movie.getName());
          orderedTickets.setMoviePreviewUrl(file.getAccessUrl());
          orderedTickets.setAgeLimit(movie.getAgeLimit());
          orderedTickets.setStartDate(showtime.getStartDate());
          orderedTickets.setCinemaRoomName(cinemaRoom.getName());
          orderedTickets.setCity(cinema.getCity());
          orderedTickets.setStreetAddress(cinema.getStreetAddress());
        });

    orderedTickets.setOrderId(order.getId());
    orderedTickets.setOwnerEmail(order.getAuditEntity().getCreatedBy());

    List<TicketDetails> ticketDetailsList = getTicketDetails(order);
    orderedTickets.setTickets(ticketDetailsList);

    return orderedTickets;
  }

  private List<TicketDetails> getTicketDetails(Order order) {
    return order.getOrderTickets().stream()
        .map(OrderTicket::getTicket)
        .map(ticket -> {
          TicketDetails ticketDetails = new TicketDetails();
          ticketDetails.setCinemaRoomName(ticket.getRoomSeat().getCinemaRoom().getName());
          ticketDetails.setRoomSeatNumber(ticket.getRoomSeat().getSeatNumber());
          ticketDetails.setPrice(ticket.getShowtime().getPrice());
          return ticketDetails;
        })
        .toList();
  }
}
