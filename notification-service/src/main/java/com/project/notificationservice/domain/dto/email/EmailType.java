package com.project.notificationservice.domain.dto.email;

import lombok.Getter;

/**
 * Enum class that distinguishes email types for different user cases.
 */
@Getter
public enum EmailType {

  VERIFICATION_EMAIL("user-verification-email.html"),
  ORDERED_TICKETS_EMAIL("ordered-tickets.html");

  private final String fileName;

  EmailType(String fileName) {
    this.fileName = fileName;
  }
}
