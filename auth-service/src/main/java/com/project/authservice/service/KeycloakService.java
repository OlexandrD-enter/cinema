package com.project.authservice.service;

import java.util.Map;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;

/**
 * KeycloakService interface for Keycloak operations.
 */
public interface KeycloakService {

  ResponseEntity<String> logout(Map<String, Object> logoutRequestParams);

  AccessTokenResponse refreshToken(Map<String, Object> refreshTokenRequestParams);
}
