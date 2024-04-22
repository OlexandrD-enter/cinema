package com.project.userservice.service;

import com.project.userservice.domain.dto.UserEmailVerificationResponse;
import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;

/**
 * AuthService interface for user authentication-related operations.
 */
public interface AuthService {

  UserRegistrationResponse createUser(UserRegistrationRequest registrationRequest);

  UserEmailVerificationResponse verifyUserEmail(String token);

}
