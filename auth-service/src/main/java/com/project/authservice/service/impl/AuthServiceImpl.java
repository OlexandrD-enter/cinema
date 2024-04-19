package com.project.authservice.service.impl;

import com.project.authservice.domain.dto.TokenResponse;
import com.project.authservice.domain.dto.UserLoginRequest;
import com.project.authservice.domain.dto.UserLogoutRequest;
import com.project.authservice.domain.dto.UserRefreshTokenRequest;
import com.project.authservice.service.AuthService;
import com.project.authservice.service.exception.EmailNotVerifiedException;
import com.project.authservice.service.exception.IncorrectCredentialsException;
import com.project.authservice.service.exception.LogoutException;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * AuthService implementation responsible for authentication-related operations.
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final Keycloak keycloak;
  private final String clientId;
  private final String authUrl;
  private final String realm;
  private final String secretKey;
  private final KeycloakServiceImpl keycloakClient;

  /**
   * Constructs a new instance of AuthServiceImpl.
   *
   * @param keycloak  The Keycloak instance used for authentication and authorization.
   * @param clientId  The client ID for the Keycloak client.
   * @param authUrl   The URL of the Keycloak authorization server.
   * @param realm     The Keycloak realm.
   * @param secretKey The secret key used for authentication with Keycloak.
   */
  public AuthServiceImpl(
      Keycloak keycloak,
      @Value("${keycloak.resource}") String clientId,
      @Value("${keycloak.auth-server-url}") String authUrl,
      @Value("${keycloak.realm}") String realm,
      @Value("${keycloak.credentials.secret}") String secretKey,
      KeycloakServiceImpl keycloakClient
  ) {
    this.keycloak = keycloak;
    this.clientId = clientId;
    this.authUrl = authUrl;
    this.realm = realm;
    this.secretKey = secretKey;
    this.keycloakClient = keycloakClient;
  }

  @Override
  public TokenResponse login(UserLoginRequest userLoginRequest) {
    List<UserRepresentation> byUsername = findByUsername(userLoginRequest.getEmail());
    if (!byUsername.isEmpty()) {
      try (Keycloak userKeycloak = keycloakCredentialBuilder(userLoginRequest)) {
        log.debug("Trying to login with email={} into keycloak", userLoginRequest.getEmail());

        UserRepresentation userRepresentation = byUsername.get(0);

        if (!userRepresentation.isEmailVerified()) {
          throw new EmailNotVerifiedException("Email is not verified");
        }
        AccessTokenResponse accessTokenResponse = userKeycloak.tokenManager().getAccessToken();
        log.debug("Successfully login with email={} into keycloak", userLoginRequest.getEmail());

        return TokenResponse.builder()
            .accessToken(accessTokenResponse.getToken())
            .refreshToken(accessTokenResponse.getRefreshToken())
            .build();
      } catch (NotAuthorizedException e) {
        throw new IncorrectCredentialsException("Username or password incorrect");
      }
    }
    throw new IncorrectCredentialsException("Username or password incorrect");
  }

  @Override
  public String logout(String refreshToken) {
    UserLogoutRequest userLogoutRequest = UserLogoutRequest.builder()
        .refreshToken(refreshToken)
        .clientId(clientId)
        .clientSecret(secretKey)
        .build();

    log.debug("Trying logout from keycloak");

    Map<String, Object> logoutRequestParams = mapLogoutRequest(userLogoutRequest);
    ResponseEntity<String> response = keycloakClient.logout(logoutRequestParams);

    if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
      log.debug("Failed logout from keycloak");
      throw new LogoutException("Failed logout from keycloak");
    }
    log.debug("Successfully logout from keycloak");

    return response.getBody();
  }

  @Override
  public TokenResponse refreshToken(String refreshToken) {
    UserRefreshTokenRequest userRefreshTokenRequest = UserRefreshTokenRequest.builder()
        .clientSecret(secretKey)
        .clientId(clientId)
        .grantType("refresh_token")
        .refreshToken(refreshToken)
        .build();

    log.debug("Trying refresh token");

    Map<String, Object> refreshTokenParams = mapRefreshToken(userRefreshTokenRequest);
    AccessTokenResponse response = keycloakClient.refreshToken(refreshTokenParams);

    log.debug("Token successfully refreshed");

    return TokenResponse.builder()
        .accessToken(response.getToken())
        .refreshToken(response.getRefreshToken())
        .build();
  }

  private List<UserRepresentation> findByUsername(String username) {
    return keycloak.realm(realm).users().search(username);
  }

  private Keycloak keycloakCredentialBuilder(UserLoginRequest request) {
    return KeycloakBuilder.builder()
        .serverUrl(authUrl)
        .realm(realm)
        .clientId(clientId)
        .clientSecret(secretKey)
        .username(request.getEmail())
        .password(request.getPassword())
        .build();
  }

  private Map<String, Object> mapLogoutRequest(UserLogoutRequest userLogoutRequest) {
    Map<String, Object> params = new HashMap<>();
    params.put("refresh_token", userLogoutRequest.getRefreshToken());
    params.put("client_id", userLogoutRequest.getClientId());
    params.put("client_secret", userLogoutRequest.getClientSecret());
    return params;
  }

  private Map<String, Object> mapRefreshToken(UserRefreshTokenRequest userRefreshTokenRequest) {
    Map<String, Object> params = new HashMap<>();
    params.put("grant_type", userRefreshTokenRequest.getGrantType());
    params.put("refresh_token", userRefreshTokenRequest.getRefreshToken());
    params.put("client_id", userRefreshTokenRequest.getClientId());
    params.put("client_secret", userRefreshTokenRequest.getClientSecret());
    return params;
  }
}
