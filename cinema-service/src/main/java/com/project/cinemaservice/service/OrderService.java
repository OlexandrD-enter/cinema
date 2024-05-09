package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.order.OrderClientDetails;
import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.domain.dto.order.OrderCreateRequest;

/**
 * OrderService interface for managing Order related operations.
 */
public interface OrderService {

  OrderClientResponse createOrder(OrderCreateRequest orderCreateRequest);

  OrderClientDetails getOrderForClient(Long orderId);
}
