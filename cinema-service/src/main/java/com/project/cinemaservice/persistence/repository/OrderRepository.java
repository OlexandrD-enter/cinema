package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Order entities in the database.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
