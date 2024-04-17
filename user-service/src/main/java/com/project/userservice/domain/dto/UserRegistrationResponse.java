package com.project.userservice.domain.dto;

import lombok.AllArgsConstructor;
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
public class UserRegistrationResponse {

  private Long id;
  private String email;
  private String firstName;
  private String lastName;
}
