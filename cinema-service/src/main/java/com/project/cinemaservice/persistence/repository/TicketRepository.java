package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Ticket entities in the database.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
