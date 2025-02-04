package com.project.userservice.service.impl;

import com.project.userservice.persistence.enums.TokenType;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.model.UserToken;
import com.project.userservice.persistence.repository.UserTokenRepository;
import com.project.userservice.service.UserTokenService;
import com.project.userservice.service.exception.NotValidUserToken;
import com.project.userservice.service.exception.UserTokenAlreadyExistsException;
import com.project.userservice.service.exception.UserTokenUpdateException;
import jakarta.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserTokenService implementation responsible for user-tokens related operations.
 */
@Service
@Slf4j
public class UserTokenServiceImpl implements UserTokenService {

  private final UserTokenRepository userActionTokenRepository;
  private final int retryIntervalTime;

  public UserTokenServiceImpl(UserTokenRepository userActionTokenRepository,
      @Value("${email.retry-interval-time}") int retryIntervalTime) {
    this.userActionTokenRepository = userActionTokenRepository;
    this.retryIntervalTime = retryIntervalTime;
  }

  @Transactional
  @Override
  public UserToken saveUserToken(User user, TokenType tokenType) {
    log.debug("Trying to save new userToken - userEmail: {}, token: {} into db",
        user.getEmail(), tokenType.name());

    if (userActionTokenRepository.findByUserAndTokenType(user,
        tokenType).isPresent()) {
      throw new UserTokenAlreadyExistsException(
          String.format("User with email=%s already has token such type", user.getEmail()));
    }

    UserToken userActionToken = UserToken.builder()
        .token(UUID.randomUUID().toString())
        .tokenType(tokenType)
        .user(user)
        .isUsed(false)
        .lastSendAt(LocalDateTime.now())
        .build();

    UserToken savedUserToken = userActionTokenRepository.save(userActionToken);

    log.debug("New userToken - userEmail: {}, token: {} saved into db",
        savedUserToken.getUser().getEmail(), savedUserToken.getToken());

    return savedUserToken;
  }

  @Transactional
  @Override
  public UserToken validateToken(String token) {
    log.debug("Trying to validate token: {}", token);
    UserToken userToken = userActionTokenRepository.findByToken(token)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Token='%s' not found", token)));

    if (userToken.getIsUsed()) {
      throw new NotValidUserToken(String.format("Token=%s already used", token));
    }

    userToken.setIsUsed(true);
    UserToken savedToken = userActionTokenRepository.save(userToken);

    log.debug("Token: {} validated", token);

    return savedToken;
  }

  @Override
  public UserToken updateTokenByUserEmail(String email, TokenType tokenType) {
    UserToken userToken = userActionTokenRepository.findByUserEmailAndTokenType(email, tokenType)
        .orElseThrow(() -> new EntityNotFoundException(
            String.format("Token with type=%s for user='%s' not found", tokenType, email)));

    if (userToken.getLastSendAt().plusMinutes(retryIntervalTime).isAfter(LocalDateTime.now())) {
      throw new UserTokenUpdateException(String.format(
          "Too many requests, try again in %d seconds",
          retryIntervalTime * 60L - calculateLeftTimeToResendEmail(userToken).toSeconds()));
    }
    userToken.setToken(UUID.randomUUID().toString());
    userToken.setIsUsed(false);
    userToken.setLastSendAt(LocalDateTime.now());

    return userActionTokenRepository.save(userToken);
  }

  private Duration calculateLeftTimeToResendEmail(UserToken userToken) {
    log.debug("Calculating left time to resend email");
    Duration duration = Duration.between(userToken.getLastSendAt(), LocalDateTime.now());
    log.debug("Left time to resend email: {}", duration);
    return duration;
  }
}
