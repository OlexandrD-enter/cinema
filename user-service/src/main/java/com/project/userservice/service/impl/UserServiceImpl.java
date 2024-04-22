package com.project.userservice.service.impl;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.mapper.UserMapper;
import com.project.userservice.persistence.enums.UserStatus;
import com.project.userservice.persistence.model.User;
import com.project.userservice.persistence.repository.UserRepository;
import com.project.userservice.service.UserService;
import com.project.userservice.service.exception.UserAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService implementation responsible for user-related operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  @Override
  public User saveUser(UserRegistrationRequest userRegistrationRequest) {
    if (userRepository.findUserByEmail(userRegistrationRequest.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException(
          String.format("User with email='%s' already exists", userRegistrationRequest.getEmail()));
    }

    log.debug("Trying to save new user {} into db", userRegistrationRequest.getEmail());
    User user = userMapper.toUser(userRegistrationRequest);
    userRepository.save(user);
    log.debug("New user {} saved into db", user.getEmail());
    return user;
  }

  @Transactional
  @Override
  public User verifyUser(String email) {
    log.debug("Trying to verify user: {}", email);

    User user = userRepository.findUserByEmail(email).orElseThrow(() -> new EntityNotFoundException(
        String.format("User with email='%s' not found", email)));
    user.setUserStatus(UserStatus.ACTIVE);
    User savedUser = userRepository.save(user);

    log.debug("User: {} verified", email);

    return savedUser;
  }
}
