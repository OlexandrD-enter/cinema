package com.project.gatewayservice.config;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for configuring routes in the gateway.
 */
@Configuration
public class GatewayConfig {

  /**
   * Configures the routes for the gateway.
   *
   * @param builder The RouteLocatorBuilder used to construct routes.
   * @return The configured RouteLocator.
   */
  @Bean
  public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        // User Service Routes
        .route("user-service", r -> r.path("/users/**", "/auth/register")
            .filters(f -> f.rewritePath("/users/", "/api/v1/users/")
                .rewritePath("/auth/register", "/api/v1/users/register"))
            .uri("lb://user-service"))
        // Auth Service Routes
        .route("auth-service", r -> r.path("/auth/**")
            .filters(f -> f.rewritePath("/auth/", "/api/v1/auth/"))
            .uri("lb://auth-service"))
        // Cinema Service Routes
        .route("cinema-service", r -> r.path("/admin/**", "/movies/**", "/orders/**")
            .filters(f -> f.rewritePath("/admin/", "/api/v1/admin/")
                .rewritePath("/movies/", "/api/v1/movies/")
                .rewritePath("/orders/", "/api/v1/orders/"))
            .uri("lb://cinema-service"))
        // Media Service Routes
        .route("media-service", r -> r.path("/files/**")
            .filters(f -> f.rewritePath("/files", "/api/v1/files"))
            .uri("lb://media-service"))
        // Payment Service Routes
        .route("payment-service", r -> r.path("/payments/**")
            .filters(f -> f.rewritePath("/payments", "/api/v1/payments"))
            .uri("lb://payment-service"))
        .build();
  }
}
