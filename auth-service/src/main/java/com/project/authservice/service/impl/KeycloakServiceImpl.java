package com.project.authservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.authservice.service.KeycloakClient;
import com.project.authservice.service.KeycloakService;
import com.project.authservice.service.exception.FeignClientRequestException;
import feign.FeignException;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Implementation of the KeycloakClient interface responsible for handling Keycloak client
 * operations.
 */
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

  private final KeycloakClient keycloakClient;
  private final ObjectMapper objectMapper;

  @Override
  public ResponseEntity<String> logout(Map<String, Object> logoutRequestParams) {
    try {
      return keycloakClient.logout(logoutRequestParams);
    } catch (FeignException e) {
      handleFeignException(e);
    }
    throw new FeignClientRequestException("Feign client request failed");
  }

  @Override
  public AccessTokenResponse refreshToken(Map<String, Object> refreshTokenRequestParams) {
    try {
      return keycloakClient.refreshToken(refreshTokenRequestParams);
    } catch (FeignException e) {
      handleFeignException(e);
    }
    throw new FeignClientRequestException("Feign client request failed");
  }

  private void handleFeignException(FeignException e) {
    if (e.status() == 400) {
      String responseBody = e.contentUTF8();
      try {
        Map<String, Object> errorResponse = objectMapper.readValue(responseBody,
            new TypeReference<>() {
            });
        String errorMessage = errorResponse.get("error_description").toString();
        throw new FeignClientRequestException(errorMessage);
      } catch (IOException ioException) {
        throw new FeignClientRequestException("Failed to parse error response " + ioException);
      }
    }
  }
}
