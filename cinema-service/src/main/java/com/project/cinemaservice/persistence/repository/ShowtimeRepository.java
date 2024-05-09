package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.domain.dto.showtime.ShowtimeStartAndEndDate;
import com.project.cinemaservice.persistence.model.Showtime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Showtime entities in the database.
 */
@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

  @Query("SELECT new com.project.cinemaservice.domain.dto.showtime.ShowtimeStartAndEndDate( "
      + "s.id, s.startDate, s.endDate) FROM Showtime s WHERE s.cinemaRoom.id=:cinemaRoomId")
  List<ShowtimeStartAndEndDate> findStartAndEndDateOfShowtimeByRoomId(Long cinemaRoomId);

  @EntityGraph(attributePaths = {"tickets", "cinemaRoom"})
  Optional<Showtime> findById(Long showtimeId);
}
