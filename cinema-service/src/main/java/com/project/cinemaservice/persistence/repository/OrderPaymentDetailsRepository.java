package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.persistence.model.OrderPaymentDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing OrderPaymentDetails entities in the database.
 */
@Repository
public interface OrderPaymentDetailsRepository extends JpaRepository<OrderPaymentDetails, Long> {
  Optional<OrderPaymentDetails> findByOrderId(Long orderId);
}
