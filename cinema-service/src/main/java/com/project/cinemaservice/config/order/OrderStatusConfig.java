package com.project.cinemaservice.config.order;

import com.project.cinemaservice.persistence.enums.OrderStatus;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for managing order status-related configurations.
 */
@Configuration
public class OrderStatusConfig {

  /**
   * Provides a bean for configuring and initializing a map representing allowed transitions
   * between different order statuses.
   *
   * @return A map where each key represents an order status, and the associated value is a set
   *     of order statuses allowed to transition to the key status.
   */
  @Bean
  public Map<OrderStatus, Set<OrderStatus>> orderStatusTransitionsMap() {
    Map<OrderStatus, Set<OrderStatus>> transitions = new EnumMap<>(OrderStatus.class);

    transitions.put(OrderStatus.CANCELLED, EnumSet.of(OrderStatus.RESERVED, OrderStatus.PAID));

    return transitions;
  }
}
