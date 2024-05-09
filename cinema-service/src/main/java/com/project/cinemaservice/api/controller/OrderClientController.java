package com.project.cinemaservice.api.controller;

import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.domain.dto.order.OrderCreateRequest;
import com.project.cinemaservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to orders.<br> Endpoints provided:<br>
 * - POST /: Creates a new order based on request data.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping("/api/v1/orders")
public class OrderClientController {

  private final OrderService orderService;

  @Operation(summary = "This method is used for order creation.")
  @PostMapping
  public ResponseEntity<OrderClientResponse> createOrder(
      @RequestBody @Valid OrderCreateRequest orderCreateRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(orderService.createOrder(orderCreateRequest));
  }
}
