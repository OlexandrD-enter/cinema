package com.project.gatewayservice.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter to pass Authorization header to downstream services.
 */
@Component
public class AuthHeaderFilter implements GlobalFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    HttpHeaders headers = exchange.getRequest().getHeaders();
    if (headers.containsKey(AUTHORIZATION_HEADER)) {
      String authToken = headers.getFirst(AUTHORIZATION_HEADER);
      exchange.getRequest().mutate()
          .headers(httpHeaders -> httpHeaders.set(AUTHORIZATION_HEADER, authToken));
    }
    return chain.filter(exchange);
  }
}
