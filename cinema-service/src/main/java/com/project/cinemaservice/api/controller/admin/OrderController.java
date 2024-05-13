package com.project.cinemaservice.api.controller.admin;

import com.project.cinemaservice.domain.dto.order.OrderBriefInfoAdmin;
import com.project.cinemaservice.domain.dto.order.OrderFilterRequest;
import com.project.cinemaservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to orders.<br> Endpoints provided:<br>
 * - GET /filters: Get a orders information base on filters.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/orders")
public class OrderController {

  private final OrderService orderService;

  @Operation(summary = "This method is used to get all orders by filters.")
  @GetMapping("/filters")
  public ResponseEntity<OrderBriefInfoAdmin> getAllOrders(
      Pageable pageable,
      @RequestBody @Valid OrderFilterRequest orderFilterRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(orderService.getAllOrdersByFilter(pageable, orderFilterRequest));
  }
}
