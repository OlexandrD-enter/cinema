package com.project.authservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.authservice.domain.dto.TokenResponse;
import com.project.authservice.domain.dto.UserLoginRequest;
import com.project.authservice.service.exception.EmailNotVerifiedException;
import com.project.authservice.service.exception.FeignClientRequestException;
import com.project.authservice.service.exception.IncorrectCredentialsException;
import com.project.authservice.service.exception.LogoutException;
import com.project.authservice.service.impl.AuthServiceImpl;
import com.project.authservice.service.impl.KeycloakServiceImpl;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

  @Mock
  private Keycloak keycloak;
  @Mock
  private UsersResource usersResource;
  @Mock
  private KeycloakServiceImpl keycloakClient;
  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    authService = new AuthServiceImpl(
        keycloak,
        "clientId",
        "http://localhost:8080",
        "realm",
        "secretKey",
        keycloakClient
    );
  }

/*  @Test
  void login_ValidUser_SuccessfulLogin() {
    // Given
    UserLoginRequest userLoginRequest = new UserLoginRequest();
    userLoginRequest.setEmail("test@example.com");
    userLoginRequest.setPassword("password");

    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setEmail(userLoginRequest.getEmail());
    userRepresentation.setEmailVerified(true);

    AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
    accessTokenResponse.setToken("accessToken");
    accessTokenResponse.setRefreshToken("refreshToken");

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm("realm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(usersResource.search(userLoginRequest.getEmail())).thenReturn(
        Collections.singletonList(userRepresentation));

    TokenManager tokenManager = mock(TokenManager.class);
    when(keycloak.tokenManager()).thenReturn(tokenManager);
    when(tokenManager.getAccessToken()).thenReturn(accessTokenResponse);

    // When
    LoginResponse loginResponse = authService.login(userLoginRequest);

    // Then
    assertNotNull(loginResponse);
    assertEquals("accessToken", loginResponse.getAccessToken());
    assertEquals("refreshToken", loginResponse.getRefreshToken());

  }*/

  @Test
  void login_UserNotFound_ThrowsIncorrectCredentialsException() {
    // Given
    UserLoginRequest userLoginRequest = new UserLoginRequest();
    userLoginRequest.setEmail("nonexistent@gmail.com");
    userLoginRequest.setPassword("password");

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm("realm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm("realm").users().search(userLoginRequest.getEmail())).thenReturn(
        Collections.emptyList());

    // When & Then
    assertThrows(IncorrectCredentialsException.class, () -> authService.login(userLoginRequest));
  }

  @Test
  void login_EmailNotVerified_ThrowsEmailNotVerifiedException() {
    // Given
    UserLoginRequest userLoginRequest = new UserLoginRequest();
    userLoginRequest.setEmail("demchenko@gmail.com");
    userLoginRequest.setPassword("password");

    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setEmail(userLoginRequest.getEmail());
    userRepresentation.setEmailVerified(false);

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm("realm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm("realm").users().search(userLoginRequest.getEmail())).thenReturn(
        Collections.singletonList(userRepresentation));

    // When & Then
    assertThrows(EmailNotVerifiedException.class, () -> authService.login(userLoginRequest));
  }

/*  @Test
  void login_IncorrectPassword_IncorrectCredentialsExceptionThrown() {
    // Given+
    UserLoginRequest userLoginRequest = new UserLoginRequest();
    userLoginRequest.setEmail("test@example.com");
    userLoginRequest.setPassword("incorrectPassword");

    UserRepresentation userRepresentation = new UserRepresentation();
    userRepresentation.setEmail(userLoginRequest.getEmail());
    userRepresentation.setEmailVerified(true);

    RealmResource realmResource = mock(RealmResource.class);
    when(keycloak.realm("realm")).thenReturn(realmResource);
    when(realmResource.users()).thenReturn(usersResource);
    when(keycloak.realm("realm").users().search(userLoginRequest.getEmail())).thenReturn(
        Collections.singletonList(userRepresentation));

    TokenManager tokenManager = mock(TokenManager.class);
    when(keycloak.tokenManager()).thenReturn(tokenManager);
    when(tokenManager.getAccessToken()).thenThrow(NotAuthorizedException.class);

    // When & Then
    assertThrows(IncorrectCredentialsException.class, () -> authService.login(userLoginRequest));
  }*/

  @Test
  void logout_SuccessfulLogout() {
    // Given
    String refreshToken = "refreshToken";
    Map<String, Object> logoutRequestParams = new HashMap<>();
    logoutRequestParams.put("refresh_token", refreshToken);
    logoutRequestParams.put("client_id", "clientId");
    logoutRequestParams.put("client_secret", "secretKey");

    ResponseEntity<String> expectedResponse = ResponseEntity.noContent().build();

    when(keycloakClient.logout(logoutRequestParams)).thenReturn(expectedResponse);

    // When
    String response = authService.logout(refreshToken);

    // Then
    assertEquals(expectedResponse.getBody(), response);
    verify(keycloakClient, times(1)).logout(logoutRequestParams);
  }

  @Test
  void logout_FailedLogout_ThrowsLogoutException() {
    // Given
    String refreshToken = "refreshToken";
    Map<String, Object> logoutRequestParams = new HashMap<>();
    logoutRequestParams.put("refresh_token", refreshToken);
    logoutRequestParams.put("client_id", "clientId");
    logoutRequestParams.put("client_secret", "secretKey");

    ResponseEntity<String> failedResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("Failed to logout");

    when(keycloakClient.logout(logoutRequestParams)).thenReturn(failedResponse);

    // When & Then
    assertThrows(LogoutException.class, () -> authService.logout(refreshToken));
  }

  @Test
  void refreshToken_SuccessfulRefresh() {
    // Given
    String refreshToken = "refreshToken";
    Map<String, Object> refreshTokenParams = new HashMap<>();
    refreshTokenParams.put("refresh_token", refreshToken);
    refreshTokenParams.put("grant_type", "refresh_token");
    refreshTokenParams.put("client_id", "clientId");
    refreshTokenParams.put("client_secret", "secretKey");

    AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
    accessTokenResponse.setToken("newAccessToken");
    accessTokenResponse.setRefreshToken("newRefreshToken");

    when(keycloakClient.refreshToken(refreshTokenParams)).thenReturn(accessTokenResponse);

    // When
    TokenResponse response = authService.refreshToken(refreshToken);

    // Then
    assertEquals("newAccessToken", response.getAccessToken());
    assertEquals("newRefreshToken", response.getRefreshToken());
    verify(keycloakClient, times(1)).refreshToken(refreshTokenParams);
  }

  @Test
  void refreshToken_FailedRefresh_ThrowsFeignClientRequestException() {
    // Given
    String refreshToken = "refreshToken";
    Map<String, Object> refreshTokenParams = new HashMap<>();
    refreshTokenParams.put("refresh_token", refreshToken);
    refreshTokenParams.put("grant_type", "refresh_token");
    refreshTokenParams.put("client_id", "clientId");
    refreshTokenParams.put("client_secret", "secretKey");

    when(keycloakClient.refreshToken(refreshTokenParams))
        .thenThrow(FeignClientRequestException.class);

    // When & Then
    assertThrows(FeignClientRequestException.class, () -> authService.refreshToken(refreshToken));
  }
}
