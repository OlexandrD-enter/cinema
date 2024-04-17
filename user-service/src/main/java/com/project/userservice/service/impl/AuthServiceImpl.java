package com.project.userservice.service.impl;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.domain.mapper.UserMapper;
import com.project.userservice.persistence.model.User;
import com.project.userservice.service.AuthService;
import com.project.userservice.service.KeycloakService;
import com.project.userservice.service.UserService;
import com.project.userservice.service.exception.KeycloakException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthService implementation responsible for authentication-related operations.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final KeycloakService keycloakService;
  private final UserService userService;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserRegistrationResponse createUser(UserRegistrationRequest registrationRequest) {
    User user = userService.saveUser(registrationRequest);

    try (Response response = keycloakService.createUser(registrationRequest)) {
      if (response.getStatus() != 201) {
        throw new KeycloakException("Unable to create user in keycloak");
      }
    }

    return userMapper.toRegistrationResponse(user);
  }
}
