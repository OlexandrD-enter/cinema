package com.project.userservice.api.controller;

import com.project.userservice.domain.dto.UserRegistrationRequest;
import com.project.userservice.domain.dto.UserRegistrationResponse;
import com.project.userservice.service.AuthService;
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
 * - /register: Creates a new user based on the provided registration request.<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "This method is used for registration. Don't use dummy email addresses, "
      + "use your real one.")
  @PostMapping("/register")
  public ResponseEntity<UserRegistrationResponse> register(
      @RequestBody @Valid UserRegistrationRequest registrationRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(authService.createUser(registrationRequest));
  }
}
