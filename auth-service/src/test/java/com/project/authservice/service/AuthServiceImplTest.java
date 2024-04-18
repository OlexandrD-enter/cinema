package com.project.authservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.project.authservice.domain.dto.UserLoginRequest;
import com.project.authservice.service.exception.EmailNotVerifiedException;
import com.project.authservice.service.exception.IncorrectCredentialsException;
import com.project.authservice.service.impl.AuthServiceImpl;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

  @Mock
  private Keycloak keycloak;
  @Mock
  private UsersResource usersResource;
  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    authService = new AuthServiceImpl(
        keycloak,
        "clientId",
        "http://localhost:8080",
        "realm",
        "secretKey"
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
  void login_UserNotFound_IncorrectCredentialsExceptionThrown() {
    // Given
    UserLoginRequest userLoginRequest = new UserLoginRequest();
    userLoginRequest.setEmail("nonexistent@example.com");
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
  void login_EmailNotVerified_EmailNotVerifiedExceptionThrown() {
    // Given
    UserLoginRequest userLoginRequest = new UserLoginRequest();
    userLoginRequest.setEmail("test@example.com");
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
}
