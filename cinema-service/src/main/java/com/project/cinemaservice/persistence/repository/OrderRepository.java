package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.domain.dto.order.OrderDetails;
import com.project.cinemaservice.persistence.model.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Order entities in the database.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

  @Query("SELECT NEW com.project.cinemaservice.domain.dto.order.OrderDetails( "
      + "o.id, o.orderStatus, SUM(st.price), m.name, mf.fileId, o.auditEntity.createdBy) "
      + "FROM Order o "
      + "LEFT JOIN o.orderTickets ot "
      + "LEFT JOIN ot.ticket t "
      + "LEFT JOIN t.showtime st "
      + "LEFT JOIN st.movie m "
      + "LEFT JOIN m.movieFiles mf "
      + "WHERE o.id = :orderId "
      + "AND mf.movieFileType = 'MOVIE_PREVIEW' "
      + "GROUP BY o.id, o.orderStatus, st.id, m.name, mf.fileId")
  Optional<OrderDetails> findOrderDetails(@Param("orderId") Long orderId);

  @Query("SELECT rs.seatNumber "
      + "FROM Order o "
      + "LEFT JOIN o.orderTickets ot "
      + "LEFT JOIN ot.ticket t "
      + "LEFT JOIN t.roomSeat rs "
      + "WHERE o.id = :orderId GROUP BY rs.seatNumber")
  List<Long> findBookedRoomSeatNumbers(@Param("orderId") Long orderId);
}
