package com.project.userservice.service.impl;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.service.KeycloakService;
import com.project.userservice.service.exception.UserAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * KeycloakService implementation responsible for Keycloak-related operations.
 */
@Service
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

  private final Keycloak keycloak;
  private final String realm;

  public KeycloakServiceImpl(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
    this.keycloak = keycloak;
    this.realm = realm;
  }

  @Override
  public Response createUser(UserRegistrationRequest request) {
    if (!findByEmail(request.getEmail()).isEmpty()) {
      throw new UserAlreadyExistsException(
          String.format("User with email='%s' already exists", request.getEmail()));
    }

    log.debug("Trying to save new user {} into keycloak", request.getEmail());
    CredentialRepresentation password = preparePasswordRepresentation(request.getPassword());
    UserRepresentation user = prepareUserRepresentation(request, password);

    Response response = keycloak.realm(realm).users().create(user);
    log.debug("New user {} saved into keycloak", user.getEmail());

    return response;
  }

  @Override
  public void verifyUser(String email) {
    log.debug("Trying to verify user {} in keycloak", email);
    List<UserRepresentation> usersByEmail = findByEmail(email);
    if (usersByEmail.isEmpty()) {
      throw new EntityNotFoundException(
          String.format("User with email='%s' not found", email));
    }
    UserRepresentation userRepresentation = usersByEmail.get(0);
    userRepresentation.setEmailVerified(true);

    keycloak.realm(realm).users().get(userRepresentation.getId())
        .update(userRepresentation);

    log.debug("User {} verified in keycloak", email);
  }

  private List<UserRepresentation> findByEmail(String email) {
    return keycloak.realm(realm).users().search(email);
  }

  private CredentialRepresentation preparePasswordRepresentation(String password) {
    CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
    credentialRepresentation.setTemporary(false);
    credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
    credentialRepresentation.setValue(password);
    return credentialRepresentation;
  }

  private UserRepresentation prepareUserRepresentation(
      UserRegistrationRequest request, CredentialRepresentation credentialRepresentation) {
    UserRepresentation newUser = new UserRepresentation();
    newUser.setUsername(request.getEmail());
    newUser.setEmail(request.getEmail());
    newUser.setEmailVerified(false);
    newUser.setFirstName(request.getFirstName());
    newUser.setLastName(request.getLastName());
    newUser.setCredentials(Collections.singletonList(credentialRepresentation));
    newUser.setEnabled(true);
    return newUser;
  }
}
