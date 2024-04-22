package com.project.notificationservice.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents the email verification data for a user.
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserEmailVerification {

  private String email;
  private String token;
}
