package com.project.gatewayservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
  @Bean
  public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        //USER-SERVICE ROUTING
        .route(r -> r.path("/auth/register").filters(f -> f
            .rewritePath("/auth/register", "/api/v1/users/register")
        ).uri("lb://user-service"))
        //AUTH-SERVICE ROUTING
        .route(r -> r.path("/auth/**").filters(f -> f
            .rewritePath("/auth/", "/api/v1/auth/")
        ).uri("lb://auth-service"))
        .build();
  }
}
