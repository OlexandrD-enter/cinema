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
        .route(r -> r.path("/admin/cinemas/**").filters(f -> f
            .rewritePath("/admin/cinemas", "/api/v1/admin/cinemas")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/admin/cinema-rooms/**").filters(f -> f
            .rewritePath("/admin/cinema-rooms", "/api/v1/admin/cinema-rooms")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/admin/room-seats/**").filters(f -> f
            .rewritePath("/admin/room-seats", "/api/v1/admin/room-seats")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/admin/genres/**").filters(f -> f
            .rewritePath("/admin/genres", "/api/v1/admin/genres")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/admin/movies/filters/**").filters(f -> f
            .rewritePath("/admin/movies/filters", "/api/v1/admin/movies/filters")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/admin/movies/**").filters(f -> f
            .rewritePath("/admin/movies", "/api/v1/admin/movies")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/movies/**").filters(f -> f
            .rewritePath("/movies", "/api/v1/movies")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/admin/showtimes/**").filters(f -> f
            .rewritePath("/admin/showtimes", "/api/v1/admin/showtimes")
        ).uri("lb://cinema-service"))
        .route(r -> r.path("/orders/**").filters(f -> f
            .rewritePath("/orders", "/api/v1/orders")
        ).uri("lb://cinema-service"))
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
