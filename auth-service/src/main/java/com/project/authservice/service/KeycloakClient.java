package com.project.authservice.service;

import feign.QueryMap;
import java.util.Map;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Feign client interface for communicating with the Keycloak API.
 */
@FeignClient(name = "keycloak-client", url = "${keycloak.base-url}")
public interface KeycloakClient {

  /**
   * Log out a user session using the provided parameters.
   *
   * @param logoutRequestParams Map containing logout request parameters.
   * @return ResponseEntity containing the result of the logout operation.
   */
  @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  ResponseEntity<String> logout(@QueryMap Map<String, ?> logoutRequestParams);

  /**
   * Refresh the user access token using the provided parameters.
   *
   * @param refreshTokenParams Map containing refresh token request parameters.
   * @return AccessTokenResponse containing the refreshed access token and new refresh token.
   */
  @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  AccessTokenResponse refreshToken(@QueryMap Map<String, ?> refreshTokenParams);
}
