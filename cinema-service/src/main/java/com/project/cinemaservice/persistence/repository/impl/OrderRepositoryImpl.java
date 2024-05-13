package com.project.cinemaservice.persistence.repository.impl;

import static com.querydsl.core.types.Projections.constructor;

import com.project.cinemaservice.domain.dto.order.OrderBriefInfo;
import com.project.cinemaservice.domain.dto.order.OrderBriefInfoAdmin;
import com.project.cinemaservice.domain.dto.order.OrderFilterRequest;
import com.project.cinemaservice.persistence.model.Order;
import com.project.cinemaservice.persistence.model.QCinema;
import com.project.cinemaservice.persistence.model.QCinemaRoom;
import com.project.cinemaservice.persistence.model.QMovie;
import com.project.cinemaservice.persistence.model.QOrder;
import com.project.cinemaservice.persistence.model.QOrderTicket;
import com.project.cinemaservice.persistence.model.QShowtime;
import com.project.cinemaservice.persistence.model.QTicket;
import com.project.cinemaservice.persistence.repository.OrderRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

/**
 * Custom implementation of OrderRepository for advanced querying.
 */
@Repository
public class OrderRepositoryImpl extends QuerydslRepositorySupport implements
    OrderRepositoryCustom {

  private static final QOrder qOrder = QOrder.order;
  private static final QMovie qMovie = QMovie.movie;
  private static final QCinema qCinema = QCinema.cinema;
  private static final QCinemaRoom qCinemaRoom = QCinemaRoom.cinemaRoom;
  private static final QOrderTicket qOrderTicket = QOrderTicket.orderTicket;
  private static final QTicket qTicket = QTicket.ticket;
  private static final QShowtime qShowtime = QShowtime.showtime;
  private final JPAQueryFactory queryFactory;

  public OrderRepositoryImpl(EntityManager entityManager) {
    super(Order.class);
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<OrderBriefInfo> findAllUserOrders(Pageable pageable, String userEmail) {

    JPAQuery<OrderBriefInfo> query = buildOrderBriefInfoQuery()
        .where(qOrder.auditEntity.createdBy.eq(userEmail))
        .orderBy(new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, qOrder.id));

    query.groupBy(qOrder.id,
        qOrder.auditEntity.createdAt,
        qOrder.orderStatus,
        qCinema.name,
        qCinema.city,
        qCinema.streetAddress,
        qMovie.name,
        qShowtime.startDate,
        qShowtime.price);

    long total = query.stream().count();

    List<OrderBriefInfo> resultList = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return new PageImpl<>(resultList, pageable, total);
  }

  @Override
  public OrderBriefInfoAdmin findAllOrders(Pageable pageable,
      OrderFilterRequest orderFilterRequest) {
    JPAQuery<OrderBriefInfo> query = buildOrderBriefInfoQuery()
        .where(getPredicateBasedOnFiltersWithoutPriceAndDate(orderFilterRequest));

    query.groupBy(qOrder.id,
        qOrder.auditEntity.createdAt,
        qOrder.orderStatus,
        qCinema.name,
        qCinema.city,
        qCinema.streetAddress,
        qMovie.name,
        qShowtime.startDate,
        qShowtime.price);

    List<BigDecimal> result =
        queryFactory.select(qShowtime.price.sum().coalesce(BigDecimal.valueOf(0)))
            .from(qOrder)
            .leftJoin(qOrder.orderTickets, qOrderTicket)
            .leftJoin(qOrderTicket.ticket, qTicket)
            .leftJoin(qTicket.showtime, qShowtime)
            .leftJoin(qShowtime.movie, qMovie)
            .leftJoin(qShowtime.cinemaRoom, qCinemaRoom)
            .leftJoin(qCinemaRoom.cinema, qCinema)
            .groupBy(qOrder.id)
            .fetch();

    Tuple minMaxCreationDateRange = queryFactory.select(
            qOrder.auditEntity.createdAt.min(),
            qOrder.auditEntity.createdAt.max())
        .from(qOrder)
        .leftJoin(qOrder.orderTickets, qOrderTicket)
        .leftJoin(qOrderTicket.ticket, qTicket)
        .leftJoin(qTicket.showtime, qShowtime)
        .leftJoin(qShowtime.movie, qMovie)
        .leftJoin(qShowtime.cinemaRoom, qCinemaRoom)
        .leftJoin(qCinemaRoom.cinema, qCinema)
        .where(getPredicateBasedOnFiltersWithoutPriceAndDate(orderFilterRequest))
        .fetchFirst();

    query.having(getPredicateBasedOnFiltersWithPriceAndDate(orderFilterRequest));
    applySorting(query, pageable.getSort());

    BigDecimal minPrice = result.stream().min(BigDecimal::compareTo).orElse(BigDecimal.valueOf(0));
    BigDecimal maxPrice = result.stream().max(BigDecimal::compareTo).orElse(BigDecimal.valueOf(0));

    LocalDateTime[] creationDates = getMinMaxCreationDate(minMaxCreationDateRange);
    LocalDateTime minCreationDate = creationDates[0];
    LocalDateTime maxCreationDate = creationDates[1];

    long total = query.stream().count();

    List<OrderBriefInfo> resultList = query
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    PageImpl<OrderBriefInfo> orderBriefInfos = new PageImpl<>(resultList, pageable, total);

    return new OrderBriefInfoAdmin(orderBriefInfos, minCreationDate, maxCreationDate, minPrice,
        maxPrice);
  }

  private JPAQuery<OrderBriefInfo> buildOrderBriefInfoQuery() {
    return queryFactory
        .select(constructor(
            OrderBriefInfo.class,
            qOrder.id,
            qOrder.auditEntity.createdAt,
            qOrder.orderStatus,
            qCinema.name,
            qCinema.city,
            qCinema.streetAddress,
            qMovie.name,
            qShowtime.startDate,
            qShowtime.price.sum().coalesce(BigDecimal.valueOf(0))
        ))
        .from(qOrder)
        .leftJoin(qOrder.orderTickets, qOrderTicket)
        .leftJoin(qOrderTicket.ticket, qTicket)
        .leftJoin(qTicket.showtime, qShowtime)
        .leftJoin(qShowtime.movie, qMovie)
        .leftJoin(qShowtime.cinemaRoom, qCinemaRoom)
        .leftJoin(qCinemaRoom.cinema, qCinema);
  }

  /**
   * Constructs a Predicate based on provided movie filters which haven`t range.
   *
   * @param orderFilterRequest The filters to be applied.
   * @return A Predicate representing the filter criteria.
   */
  public Predicate getPredicateBasedOnFiltersWithoutPriceAndDate(
      OrderFilterRequest orderFilterRequest) {
    BooleanBuilder predicateBuilder = new BooleanBuilder();

    if (Objects.nonNull(orderFilterRequest.getCinemaName())) {
      predicateBuilder.and(qCinema.name.eq(orderFilterRequest.getCinemaName()));
    }

    if (Objects.nonNull(orderFilterRequest.getCinemaCity())) {
      predicateBuilder.and(qCinema.city.eq(orderFilterRequest.getCinemaCity()));
    }

    if (Objects.nonNull(orderFilterRequest.getMovieName())) {
      predicateBuilder.and(qMovie.name.eq(orderFilterRequest.getMovieName()));
    }

    return predicateBuilder;
  }

  /**
   * Constructs a Predicate based on provided movie filters which have range.
   *
   * @param orderFilterRequest The filters to be applied.
   * @return A Predicate representing the filter criteria.
   */
  public Predicate getPredicateBasedOnFiltersWithPriceAndDate(
      OrderFilterRequest orderFilterRequest) {
    BooleanBuilder predicateBuilder = new BooleanBuilder();

    if (Objects.nonNull(orderFilterRequest.getFromOrderCreationTime()) && Objects.nonNull(
        orderFilterRequest.getToOrderCreationTime())) {
      predicateBuilder.and(
          qOrder.auditEntity.createdAt.between(orderFilterRequest.getFromOrderCreationTime(),
              orderFilterRequest.getToOrderCreationTime()));
    }

    if (Objects.nonNull(orderFilterRequest.getFromPrice()) && Objects.nonNull(
        orderFilterRequest.getToPrice())) {
      predicateBuilder.and(
          qShowtime.price.sum().coalesce(BigDecimal.valueOf(0))
              .between(orderFilterRequest.getFromPrice(), orderFilterRequest.getToPrice()));
    }

    return predicateBuilder;
  }

  private void applySorting(JPAQuery<OrderBriefInfo> query, Sort sort) {
    OrderSpecifier<?> orderSpecifier = sort.isSorted()
        ? createOrderSpecifier(sort.iterator().next())
        : new OrderSpecifier<>(com.querydsl.core.types.Order.DESC, qOrder.id);
    query.orderBy(orderSpecifier);
  }

  private OrderSpecifier<?> createOrderSpecifier(
      Sort.Order order) {

    Optional<Expression<?>> sortAttribute = getSortAttribute(order.getProperty());

    return sortAttribute.map(expression -> order.getDirection() == Sort.Direction.ASC
            ? new OrderSpecifier(com.querydsl.core.types.Order.ASC, expression)
            : new OrderSpecifier(com.querydsl.core.types.Order.DESC, expression))
        .orElse(new OrderSpecifier(com.querydsl.core.types.Order.DESC, qOrder.id));
  }

  private Optional<Expression<?>> getSortAttribute(String productSortAttribute) {
    return switch (productSortAttribute.toLowerCase()) {
      case "price" -> Optional.of(qShowtime.price.sum().coalesce(BigDecimal.valueOf(0)));
      case "id" -> Optional.of(qOrder.id);
      case "date" -> Optional.of(qOrder.auditEntity.createdAt);
      default -> Optional.empty();
    };
  }

  private LocalDateTime[] getMinMaxCreationDate(Tuple minMaxCreationDate) {

    LocalDateTime minCreationDate =
        Objects.nonNull(minMaxCreationDate) ? minMaxCreationDate.get(0, LocalDateTime.class)
            : LocalDateTime.now();
    LocalDateTime maxCreationDate =
        Objects.nonNull(minMaxCreationDate) ? minMaxCreationDate.get(1, LocalDateTime.class)
            : LocalDateTime.now();

    return new LocalDateTime[]{minCreationDate, maxCreationDate};
  }
}
