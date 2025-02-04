package com.project.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.project.userservice.domain.dto.UserEmailVerificationResponse;
import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.domain.mapper.UserMapper;
import com.project.userservice.messaging.UserEventPublisher;
import com.project.userservice.messaging.event.UserEmailVerificationEvent;
import com.project.userservice.persistence.enums.TokenType;
import com.project.userservice.persistence.enums.UserStatus;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.model.UserToken;
import com.project.userservice.service.exception.KeycloakException;
import com.project.userservice.service.exception.NotValidUserToken;
import com.project.userservice.service.exception.UserTokenUpdateException;
import com.project.userservice.service.impl.AuthServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

  @Mock
  private KeycloakService keycloakService;
  @Mock
  private UserService userService;
  @Mock
  private UserTokenService userTokenService;
  @Mock
  private UserEventPublisher publisher;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void createUser_NewUser_SuccessfullyCreated() {
    // Given
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .password("password")
        .build();

    User user = new User();
    user.setEmail(userRegistrationRequest.getEmail());

    UserRegistrationResponse expectedResponse = new UserRegistrationResponse();
    expectedResponse.setEmail(user.getEmail());

    UserToken userToken = UserToken.builder()
        .token(UUID.randomUUID().toString())
        .user(user)
        .tokenType(TokenType.EMAIL_VERIFICATION)
        .build();

    Response keycloakResponse = Response.status(Status.CREATED).build();

    when(userService.saveUser(userRegistrationRequest)).thenReturn(user);
    when(userTokenService.saveUserToken(user, TokenType.EMAIL_VERIFICATION)).thenReturn(userToken);
    when(keycloakService.createUser(userRegistrationRequest)).thenReturn(keycloakResponse);
    when(userMapper.toRegistrationResponse(user)).thenReturn(expectedResponse);

    // When
    UserRegistrationResponse response = authService.createUser(userRegistrationRequest);

    // Then
    assertEquals(expectedResponse.getEmail(), response.getEmail());
    verify(publisher, times(1)).sendEmailVerificationEvent(any(UserEmailVerificationEvent.class));
  }

  @Test
  void createUser_ThrowsKeycloakException() {
    // Given
    UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.builder()
        .email("demchenko@gmail.com")
        .firstName("Oleksandr")
        .lastName("Demchenko")
        .password("password")
        .build();

    User user = new User();
    user.setEmail(userRegistrationRequest.getEmail());

    Response keycloakResponse = Response.status(Status.INTERNAL_SERVER_ERROR).build();

    when(userService.saveUser(userRegistrationRequest)).thenReturn(user);
    when(keycloakService.createUser(userRegistrationRequest)).thenReturn(keycloakResponse);

    // When & Then
    assertThrows(KeycloakException.class, () -> authService.createUser(userRegistrationRequest));
  }

  @Test
  void verifyUserEmail_ValidToken_UserEmailVerifiedSuccessfully() {
    // Given
    String token = "validToken";
    String email = "demchenko@gmail.com";

    User user = User.builder().email(email).build();
    UserToken userToken = UserToken.builder()
        .id(1L)
        .token(token)
        .user(user)
        .build();

    UserEmailVerificationResponse userEmailVerificationResponse =
        new UserEmailVerificationResponse(user.getEmail(), UserStatus.ACTIVE);

    when(userTokenService.validateToken(token)).thenReturn(userToken);
    when(userService.verifyUser(email)).thenReturn(user);
    when(userMapper.toEmailVerificationResponse(user)).thenReturn(userEmailVerificationResponse);

    // When
    UserEmailVerificationResponse response = authService.verifyUserEmail(token);

    // Then
    assertNotNull(response);

    verify(userTokenService, times(1)).validateToken(token);
    verify(userService, times(1)).verifyUser("demchenko@gmail.com");
    verify(keycloakService, times(1)).verifyUser("demchenko@gmail.com");
    verify(userMapper, times(1)).toEmailVerificationResponse(user);
  }

  @Test
  void verifyUserEmail_InvalidToken_NotValidUserTokenExceptionThrown() {
    // Given
    String token = "invalidToken";

    when(userTokenService.validateToken(token)).thenThrow(new NotValidUserToken("Invalid token"));

    // When & Then
    assertThrows(NotValidUserToken.class, () -> authService.verifyUserEmail(token));

    verify(userTokenService, times(1)).validateToken(token);
    verifyNoInteractions(userService);
    verifyNoInteractions(keycloakService);
    verifyNoInteractions(userMapper);
  }

  @Test
  void verifyUserEmail_ValidToken_UserNotFound_EntityNotFoundExceptionThrown() {
    // Given
    String token = "validToken";
    String email = "demchenko@gmail.com";

    User user = User.builder().email(email).build();
    UserToken userToken = UserToken.builder()
        .id(1L)
        .token(token)
        .user(user)
        .build();

    when(userTokenService.validateToken(token)).thenReturn(userToken);
    when(userService.verifyUser("demchenko@gmail.com")).thenThrow(
        new EntityNotFoundException("User not found"));

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> authService.verifyUserEmail(token));

    verify(userTokenService, times(1)).validateToken(token);
    verify(userService, times(1)).verifyUser("demchenko@gmail.com");
    verifyNoInteractions(keycloakService);
    verifyNoInteractions(userMapper);
  }

  @Test
  void resendEmailConfirmation_EmailConfirmationResentSuccessfully() {
    // Given
    String email = "demchenko@gmail.com";
    UserToken userToken = UserToken.builder()
        .id(1L)
        .token("oldToken")
        .user(User.builder().email(email).build())
        .tokenType(TokenType.EMAIL_VERIFICATION)
        .isUsed(false)
        .lastSendAt(LocalDateTime.now().minusHours(1))
        .build();

    when(userTokenService.updateTokenByUserEmail(email, TokenType.EMAIL_VERIFICATION)).thenReturn(
        userToken);

    // When
    authService.resendEmailConfirmation(email);

    // Then
    verify(userTokenService, times(1)).updateTokenByUserEmail(email, TokenType.EMAIL_VERIFICATION);
    verify(publisher, times(1)).sendEmailVerificationEvent(any(UserEmailVerificationEvent.class));
  }

  @Test
  void resendEmailConfirmation_TokenUpdatedTooSoon_ThrowsUserTokenUpdateException() {
    // Given
    String email = "demchenko@gmail.com";

    when(userTokenService.updateTokenByUserEmail(email, TokenType.EMAIL_VERIFICATION))
        .thenThrow(new UserTokenUpdateException("Token update too soon"));

    // When & Then
    assertThrows(UserTokenUpdateException.class, () -> authService.resendEmailConfirmation(email));

    verify(userTokenService, times(1)).updateTokenByUserEmail(email, TokenType.EMAIL_VERIFICATION);
    verify(publisher, never()).sendEmailVerificationEvent(any(UserEmailVerificationEvent.class));
  }

  @Test
  void resendEmailConfirmation_TokenNotFound_ThrowsEntityNotFoundException() {
    // Given
    String email = "demchenko@gmail.com";

    when(userTokenService.updateTokenByUserEmail(email, TokenType.EMAIL_VERIFICATION))
        .thenThrow(new EntityNotFoundException("Token not found"));

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> authService.resendEmailConfirmation(email));

    verify(userTokenService, times(1)).updateTokenByUserEmail(email, TokenType.EMAIL_VERIFICATION);
    verify(publisher, never()).sendEmailVerificationEvent(any(UserEmailVerificationEvent.class));
  }
}

