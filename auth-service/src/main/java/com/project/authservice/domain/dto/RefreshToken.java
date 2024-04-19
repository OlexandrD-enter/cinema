package com.project.authservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * Represents a request object used for refresh token and logout endpoints.
 */
@Getter
public class RefreshToken {
  @NotNull(message = "Refresh token should be not null")
  @Schema(example = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiAYxNDcw")
  private String refreshToken;
}
