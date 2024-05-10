package com.project.paymentservice.config.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Configuration class for WebClient.
 */
@Configuration
public class WebClientConfig {

  public static final int TIMEOUT = 5000;

  @Value("${cinema-service.url}")
  private String cinemaServiceUrl;

  @LoadBalanced
  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  /**
   * Configures a WebClient for the media service.
   *
   * @return WebClient for the media service
   */
  @Bean(name = "cinemaWebClient")
  public WebClient cinemaServiceWebClient() {
    return webClientBuilder()
        .baseUrl(cinemaServiceUrl)
        .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
        .filter((request, next) -> {
          String jwtToken = extractJwtTokenFromContext();
          if (jwtToken == null) {
            return next.exchange(request);
          }
          ClientRequest newRequest = ClientRequest.from(request)
              .header("Authorization", "Bearer " + jwtToken)
              .build();
          return next.exchange(newRequest);
        })
        .build();
  }

  private HttpClient getHttpClient() {
    return HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
        .doOnConnected(conn -> {
          conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
          conn.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
        });
  }

  private String extractJwtTokenFromContext() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof Jwt jwt) {
      return jwt.getTokenValue();
    }
    return null;
  }
}
