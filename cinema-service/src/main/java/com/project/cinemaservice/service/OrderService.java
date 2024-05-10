package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.order.OrderClientDetails;
import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.domain.dto.order.OrderCreateRequest;
import com.project.cinemaservice.domain.dto.order.OrderStatusDetails;

/**
 * OrderService interface for managing Order related operations.
 */
public interface OrderService {

  OrderClientResponse createOrder(OrderCreateRequest orderCreateRequest);

  OrderClientDetails getOrderForClient(Long orderId);

  OrderStatusDetails confirmOrderPayment(Long orderId, String transactionId);
  OrderStatusDetails checkIfOrderPaid(Long orderId);
  OrderStatusDetails cancelOrder(Long orderId);
}
