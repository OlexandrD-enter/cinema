package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.order.OrderBriefInfo;
import com.project.cinemaservice.domain.dto.order.OrderBriefInfoAdmin;
import com.project.cinemaservice.domain.dto.order.OrderClientDetails;
import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.domain.dto.order.OrderCreateRequest;
import com.project.cinemaservice.domain.dto.order.OrderFilterRequest;
import com.project.cinemaservice.domain.dto.order.OrderStatusDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * OrderService interface for managing Order related operations.
 */
public interface OrderService {

  OrderClientResponse createOrder(OrderCreateRequest orderCreateRequest);

  OrderClientDetails getOrderForClient(Long orderId);

  OrderStatusDetails confirmOrderPayment(Long orderId, String transactionId);

  OrderStatusDetails checkIfOrderPaid(Long orderId);

  OrderStatusDetails cancelOrder(Long orderId);

  Page<OrderBriefInfo> getAllOrdersForClient(Pageable pageable);

  OrderBriefInfoAdmin getAllOrdersByFilter(Pageable pageable,
      OrderFilterRequest orderFilterRequest);
}
