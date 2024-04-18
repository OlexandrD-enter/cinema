package com.project.authservice.service.impl;

import com.project.authservice.domain.dto.LoginResponse;
import com.project.authservice.domain.dto.UserLoginRequest;
import com.project.authservice.service.AuthService;
import com.project.authservice.service.exception.EmailNotVerifiedException;
import com.project.authservice.service.exception.IncorrectCredentialsException;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
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
      @Value("${keycloak.credentials.secret}") String secretKey
  ) {
    this.keycloak = keycloak;
    this.clientId = clientId;
    this.authUrl = authUrl;
    this.realm = realm;
    this.secretKey = secretKey;
  }

  @Override
  public LoginResponse login(UserLoginRequest userLoginRequest) {
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

        return LoginResponse.builder()
            .accessToken(accessTokenResponse.getToken())
            .refreshToken(accessTokenResponse.getRefreshToken())
            .build();
      } catch (NotAuthorizedException e) {
        throw new IncorrectCredentialsException("Username or password incorrect");
      }
    }
    throw new IncorrectCredentialsException("Username or password incorrect");
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
}
