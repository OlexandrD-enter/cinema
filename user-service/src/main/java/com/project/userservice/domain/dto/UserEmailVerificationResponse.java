package com.project.userservice.domain.dto;

import com.project.userservice.persistence.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response object used for user email verification.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEmailVerificationResponse {

  private String email;
  private UserStatus userStatus;
}
