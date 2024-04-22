package com.project.userservice.service;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import jakarta.ws.rs.core.Response;

/**
 * KeycloakService interface for Keycloak-related operations.
 */
public interface KeycloakService {

  Response createUser(UserRegistrationRequest registrationRequest);

  void verifyUser(String email);
}
