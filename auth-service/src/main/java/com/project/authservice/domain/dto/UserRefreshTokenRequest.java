package com.project.authservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request object used for refreshing access tokens.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRefreshTokenRequest {
  private String grantType;
  private String refreshToken;
  private String clientId;
  private String clientSecret;
}
