package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.CinemaRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing CinemaRoom entities in the database.
 */
@Repository
public interface CinemaRoomRepository extends JpaRepository<CinemaRoom, Long> {

  Optional<CinemaRoom> findByNameAndCinemaId(String roomName, Long cinemaId);

  @EntityGraph(attributePaths = {"roomSeats"})
  Optional<CinemaRoom> findById(Long id);
}
