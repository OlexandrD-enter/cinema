package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.RoomSeat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing CinemaRoom entities in the database.
 */
@Repository
public interface RoomSeatRepository extends JpaRepository<RoomSeat, Long> {

  Optional<RoomSeat> findBySeatNumberAndCinemaRoomId(Long seatNumber, Long roomId);
}
