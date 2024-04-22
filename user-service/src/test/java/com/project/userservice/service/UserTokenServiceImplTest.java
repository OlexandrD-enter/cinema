package com.project.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.userservice.persistence.enums.TokenType;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.model.UserToken;
import com.project.userservice.persistence.repository.UserTokenRepository;
import com.project.userservice.service.exception.NotValidUserToken;
import com.project.userservice.service.exception.UserTokenAlreadyExistsException;
import com.project.userservice.service.impl.UserTokenServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserTokenServiceImplTest {

  @Mock
  private UserTokenRepository userTokenRepository;
  private UserTokenServiceImpl userTokenService;

  @BeforeEach
  void setUp() {
    userTokenService = new UserTokenServiceImpl(userTokenRepository, 5);
  }

  @Test
  void saveUserToken_NewUserToken_SuccessfullySaved() {
    // Given
    User user = User.builder().email("demchenko@gmail.com").build();
    TokenType tokenType = TokenType.EMAIL_VERIFICATION;

    when(userTokenRepository.findByUserAndTokenType(user, tokenType)).thenReturn(Optional.empty());

    UserToken savedToken = UserToken.builder()
        .token(UUID.randomUUID().toString())
        .tokenType(tokenType)
        .user(user)
        .isUsed(false)
        .lastSendAt(LocalDateTime.now())
        .build();

    when(userTokenRepository.save(any(UserToken.class))).thenReturn(savedToken);

    // When
    UserToken result = userTokenService.saveUserToken(user, tokenType);

    // Then
    assertNotNull(result);
    assertEquals(user.getEmail(), result.getUser().getEmail());
    assertEquals(tokenType, result.getTokenType());
    assertFalse(result.getIsUsed());
    assertNotNull(result.getLastSendAt());

    verify(userTokenRepository, times(1)).findByUserAndTokenType(user, tokenType);
    verify(userTokenRepository, times(1)).save(any(UserToken.class));
  }

  @Test
  void saveUserToken_UserAlreadyHasToken_ThrowsUserTokenAlreadyExistsException() {
    // Given
    User user = User.builder().email("demchenko@gmail.com").build();
    TokenType tokenType = TokenType.EMAIL_VERIFICATION;

    UserToken existingToken = UserToken.builder()
        .token(UUID.randomUUID().toString())
        .tokenType(tokenType)
        .user(user)
        .isUsed(false)
        .lastSendAt(LocalDateTime.now())
        .build();

    when(userTokenRepository.findByUserAndTokenType(user, tokenType)).thenReturn(
        Optional.of(existingToken));

    // When & Then
    assertThrows(
        UserTokenAlreadyExistsException.class,
        () -> userTokenService.saveUserToken(user, tokenType));

    verify(userTokenRepository, times(1)).findByUserAndTokenType(user, tokenType);
    verify(userTokenRepository, never()).save(any(UserToken.class));
  }

  @Test
  void validateToken_ValidTokenAndNotUsed_SuccessfullyCheck() {
    //Given
    String token = "validToken";
    UserToken userToken = UserToken.builder()
        .id(1L)
        .token(token)
        .isUsed(false)
        .build();

    when(userTokenRepository.findByToken(token)).thenReturn(Optional.of(userToken));
    when(userTokenRepository.save(any(UserToken.class))).thenReturn(userToken);

    // When
    UserToken result = userTokenService.validateToken(token);

    // Then
    assertTrue(result.getIsUsed());
    verify(userTokenRepository, times(1)).findByToken(token);
    verify(userTokenRepository, times(1)).save(userToken);
  }

  @Test
  void validateToken_ValidTokenAlreadyUsed_ThrowsNotValidUserTokenException() {
    // Given
    String token = "validToken";
    UserToken userToken = UserToken.builder()
        .id(1L)
        .token(token)
        .isUsed(true) // Already used
        .build();

    when(userTokenRepository.findByToken(token)).thenReturn(Optional.of(userToken));

    // When & Then
    assertThrows(NotValidUserToken.class, () -> userTokenService.validateToken(token));
    verify(userTokenRepository, times(1)).findByToken(token);
    verify(userTokenRepository, never()).save(any(UserToken.class));
  }

  @Test
  void validateToken_InvalidToken_ThrowsEntityNotFoundException() {
    // Given
    String token = "invalidToken";

    when(userTokenRepository.findByToken(token)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(EntityNotFoundException.class, () -> userTokenService.validateToken(token));
    verify(userTokenRepository, times(1)).findByToken(token);
    verify(userTokenRepository, never()).save(any(UserToken.class));
  }
}
