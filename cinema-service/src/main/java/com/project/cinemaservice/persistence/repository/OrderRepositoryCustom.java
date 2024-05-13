package com.project.cinemaservice.persistence.repository;

import com.project.cinemaservice.domain.dto.order.OrderBriefInfo;
import com.project.cinemaservice.domain.dto.order.OrderBriefInfoAdmin;
import com.project.cinemaservice.domain.dto.order.OrderFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for advanced querying Order entities.
 */
@Repository
public interface OrderRepositoryCustom {

  Page<OrderBriefInfo> findAllUserOrders(Pageable pageable, String userEmail);

  OrderBriefInfoAdmin findAllOrders(Pageable pageable, OrderFilterRequest orderFilterRequest);
}
