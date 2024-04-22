package com.project.userservice.service.impl;

import com.project.userservice.domain.dto.UserEmailVerificationResponse;
import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.domain.mapper.UserMapper;
import com.project.userservice.messaging.UserEventPublisher;
import com.project.userservice.persistence.enums.TokenType;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.model.UserToken;
import com.project.userservice.service.AuthService;
import com.project.userservice.service.KeycloakService;
import com.project.userservice.service.UserService;
import com.project.userservice.service.UserTokenService;
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
  private final UserTokenService userTokenService;
  private final UserMapper userMapper;
  private final UserEventPublisher publisher;

  @Transactional
  @Override
  public UserRegistrationResponse createUser(UserRegistrationRequest registrationRequest) {
    User user = userService.saveUser(registrationRequest);
    UserToken userToken = userTokenService.saveUserToken(user, TokenType.EMAIL_VERIFICATION);

    try (Response response = keycloakService.createUser(registrationRequest)) {
      if (response.getStatus() != 201) {
        throw new KeycloakException("Unable to create user in keycloak");
      }
    }

    publisher.sendEmailVerificationEvent(user.getEmail(), userToken.getToken());

    return userMapper.toRegistrationResponse(user);
  }

  @Transactional
  @Override
  public UserEmailVerificationResponse verifyUserEmail(String token) {
    UserToken userToken = userTokenService.validateToken(token);

    String userEmail = userToken.getUser().getEmail();

    User user = userService.verifyUser(userEmail);
    keycloakService.verifyUser(userEmail);

    return userMapper.toEmailVerificationResponse(user);
  }
}
