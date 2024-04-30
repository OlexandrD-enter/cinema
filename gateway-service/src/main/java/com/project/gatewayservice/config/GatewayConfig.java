package com.project.gatewayservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for configuring routes in the gateway.
 */
@Configuration
@RequiredArgsConstructor
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
        //USER-SERVICE ROUTING
        .route(r -> r.path("/users/**").filters(f -> f
            .rewritePath("/users/", "/api/v1/users/")
        ).uri("lb://user-service"))
        .route(r -> r.path("/auth/register").filters(f -> f
            .rewritePath("/auth/register", "/api/v1/users/register")
        ).uri("lb://user-service"))
        //AUTH-SERVICE ROUTING
        .route(r -> r.path("/auth/**").filters(f -> f
            .rewritePath("/auth/", "/api/v1/auth/")
        ).uri("lb://auth-service"))
        //CINEMA-SERVICE ROUTING
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
        .route(r -> r.path("/admin/movies/**").filters(f -> f
            .rewritePath("/admin/movies", "/api/v1/admin/movies")
        ).uri("lb://cinema-service"))
        //CINEMA-SERVICE ROUTING
        .route(r -> r.path("/admin/files/**").filters(f -> f
            .rewritePath("/admin/files", "/api/v1/admin/files")
        ).uri("lb://media-service"))
        .build();
  }
}
