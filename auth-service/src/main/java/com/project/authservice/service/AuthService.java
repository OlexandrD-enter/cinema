package com.project.authservice.service;

import com.project.authservice.domain.dto.LoginResponse;
import com.project.authservice.domain.dto.UserLoginRequest;

/**
 * AuthService interface for user authentication-related operations.
 */
public interface AuthService {

  LoginResponse login(UserLoginRequest userLoginRequest);
}
