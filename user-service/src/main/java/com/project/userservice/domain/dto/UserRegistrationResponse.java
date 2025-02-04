package com.project.userservice.domain.dto;

import com.project.userservice.persistence.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a response object used for user registration.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRegistrationResponse {

  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private UserStatus userStatus;
}
