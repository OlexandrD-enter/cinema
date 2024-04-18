package com.project.authservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request user data for login.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {

  @Pattern(regexp = "^(?=.{6,30}@.{4,30}$)"
      + "[a-zA-Z0-9]+(?:[+.-]?[a-zA-Z0-9]+)*"
      + "@[a-zA-Z0-9]+(?:[.-]?[a-zA-Z0-9]+)*$",
      message = "Not valid email")
  @NotNull(message = "Not valid email")
  @Schema(example = "be-admin@gmail.com")
  private String email;
  @NotNull(message = "Not valid password")
  @Schema(example = "admin")
  private String password;
}
