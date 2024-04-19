package com.project.authservice.service;

import com.project.authservice.domain.dto.TokenResponse;
import com.project.authservice.domain.dto.UserLoginRequest;
import com.project.authservice.domain.dto.UserLogoutRequest;

/**
 * AuthService interface for user authentication-related operations.
 */
public interface AuthService {

  TokenResponse login(UserLoginRequest userLoginRequest);

  String logout(String refreshToken);

  TokenResponse refreshToken(String refreshToken);
}
