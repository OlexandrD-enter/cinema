package com.project.authservice.api.controller;

import com.project.authservice.domain.dto.LoginResponse;
import com.project.authservice.domain.dto.UserLoginRequest;
import com.project.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling authentication-related endpoints. Endpoints provided:<br>
 * - /login: Authenticates a user based on the login request.<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "This method is used for login.")
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
  }
}
