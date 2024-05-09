package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo;
import com.project.cinemaservice.persistence.model.OrderTicket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for managing OrderTicket entities in the database.
 */
public interface OrderTicketRepository extends JpaRepository<OrderTicket, Long> {

  @Query("SELECT new com.project.cinemaservice.domain.dto.roomseat.RoomSeatBriefInfo( "
      + "t.roomSeat.id, t.roomSeat.seatNumber) FROM OrderTicket ot "
      + "LEFT JOIN ot.order o "
      + "LEFT JOIN ot.ticket t "
      + "WHERE t.showtime.id = :showtimeId AND o.orderStatus IN ('RESERVED', 'PAID')")
  List<RoomSeatBriefInfo> findAllByTicketShowtimeAndOrderStatusReservedOrPaid(Long showtimeId);
}
