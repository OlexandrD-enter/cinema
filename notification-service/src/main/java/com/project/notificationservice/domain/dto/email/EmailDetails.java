package com.project.notificationservice.domain.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An abstract class representing common details for an email message.
 */
@AllArgsConstructor
@Getter
public abstract class EmailDetails {

  private String email;
  private EmailType type;
}
