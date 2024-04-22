package com.project.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.userservice.persistence.enums.TokenType;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.model.UserToken;
import com.project.userservice.persistence.repository.UserTokenRepository;
import com.project.userservice.service.exception.UserTokenAlreadyExistsException;
import com.project.userservice.service.impl.UserTokenServiceImpl;
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
}
