package com.project.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.authservice.service.exception.FeignClientRequestException;
import com.project.authservice.service.impl.KeycloakServiceImpl;
import feign.FeignException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class KeycloakServiceImplTest {

  @Mock
  private KeycloakClient keycloakClient;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private KeycloakServiceImpl keycloakService;

  @Test
  void logout_SuccessfulLogout_ReturnsResponseEntity() {
    // Given
    Map<String, Object> logoutRequestParams = new HashMap<>();
    ResponseEntity<String> expectedResponse = ResponseEntity.ok("Logout successful");

    when(keycloakClient.logout(logoutRequestParams)).thenReturn(expectedResponse);

    // When
    ResponseEntity<String> actualResponse = keycloakService.logout(logoutRequestParams);

    // Then
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @SneakyThrows
  void logout_FeignExceptionWithBadRequest_ThrowsFeignClientRequestException() {
    // Given
    Map<String, Object> logoutRequestParams = new HashMap<>();
    FeignException feignException = mock(FeignException.class);

    when(keycloakClient.logout(logoutRequestParams)).thenThrow(feignException);
    when(feignException.status()).thenReturn(400);
    when(feignException.contentUTF8()).thenReturn(
        "{\"error_description\": \"Invalid refresh token\"}");
    when(objectMapper.readValue(anyString(), any(TypeReference.class)))
        .thenReturn(Collections.singletonMap("error_description", "Invalid refresh token"));

    // When & Then
    assertThrows(FeignClientRequestException.class,
        () -> keycloakService.logout(logoutRequestParams));
  }

  @Test
  @SneakyThrows
  void logout_FeignExceptionWithServerError_ThrowsFeignClientRequestException() {
    // Given
    Map<String, Object> logoutRequestParams = new HashMap<>();
    FeignException feignException = mock(FeignException.class);

    when(keycloakClient.logout(logoutRequestParams)).thenThrow(feignException);
    when(feignException.status()).thenReturn(500);

    // When & Then
    assertThrows(FeignClientRequestException.class,
        () -> keycloakService.logout(logoutRequestParams));
  }

  @Test
  void refreshToken_SuccessfulRefresh_ReturnsAccessTokenResponse() {
    // Given
    Map<String, Object> refreshTokenRequestParams = new HashMap<>();
    AccessTokenResponse expectedResponse = new AccessTokenResponse();
    expectedResponse.setToken("accessToken");
    expectedResponse.setRefreshToken("refreshToken");

    when(keycloakClient.refreshToken(refreshTokenRequestParams)).thenReturn(expectedResponse);

    // When
    AccessTokenResponse actualResponse = keycloakService.refreshToken(refreshTokenRequestParams);

    // Then
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  @SneakyThrows
  void refreshToken_FeignExceptionWithBadRequest_ThrowsFeignClientRequestException() {
    // Given
    Map<String, Object> refreshTokenRequestParams = new HashMap<>();

    FeignException feignException = mock(FeignException.class);

    when(keycloakClient.refreshToken(refreshTokenRequestParams)).thenThrow(feignException);
    when(feignException.status()).thenReturn(400);
    when(feignException.contentUTF8()).thenReturn(
        "{\"error_description\": \"Invalid refresh token\"}");
    when(objectMapper.readValue(anyString(), any(TypeReference.class)))
        .thenReturn(Collections.singletonMap("error_description", "Invalid refresh token"));

    // When & Then
    assertThrows(FeignClientRequestException.class,
        () -> keycloakService.refreshToken(refreshTokenRequestParams));
  }

  @Test
  @SneakyThrows
  void refreshToken_FeignExceptionWithServerError_ThrowsFeignClientRequestException() {
    // Given
    Map<String, Object> refreshTokenRequestParams = new HashMap<>();

    FeignException feignException = mock(FeignException.class);

    when(keycloakClient.refreshToken(refreshTokenRequestParams)).thenThrow(feignException);
    when(feignException.status()).thenReturn(500);

    // When & Then
    assertThrows(FeignClientRequestException.class,
        () -> keycloakService.refreshToken(refreshTokenRequestParams));
  }
}
