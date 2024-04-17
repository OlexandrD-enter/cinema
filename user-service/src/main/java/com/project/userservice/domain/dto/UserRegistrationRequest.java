package com.project.userservice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request object used for user registration.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationRequest {
  @NotNull(message = "Not valid email")
  @Pattern(regexp = "^(?=.{6,30}@.{4,30}$)"
      + "[a-zA-Z0-9]+(?:[+.-]?[a-zA-Z0-9]+)*"
      + "@[a-zA-Z0-9]+((?:[-]*[a-zA-Z0-9])*"
      + "(?:[.][a-zA-Z]{2,})+(?:[0-9-]+[a-zA-z]+)*)+$",
      message = "Not valid email")
  private String email;

  @Schema(example = "Test12345678@")
  @NotNull(message = "Not valid password")
  @Pattern(
      regexp = "^(?=.*[0-9])"
          + "(?=.*[a-z])"
          + "(?=.*[A-Z])"
          + "(?=.*[~`{}\\[\\]!?%^*|\\\\@/:;<>.\\-_=+,#&$()])"
          + "(?=\\S+$)"
          + ".{6,128}$",
      message = "Not valid password"
  )
  private String password;

  @Schema(example = "John-Dear")
  @NotNull(message = "Not valid firstname")
  @Pattern(regexp = "^(?!-)[A-Za-z](?:[A-Za-z]|-(?=[A-Za-z])){1,29}(?<!-)$",
      message = "Not valid firstname")
  private String firstName;

  @Schema(example = "Johnson")
  @NotNull(message = "Not valid lastname")
  @Pattern(regexp = "^(?!-)[A-Za-z](?:[A-Za-z]|-(?=[A-Za-z])){1,29}(?<!-)$",
      message = "Not valid lastname")
  private String lastName;
}
