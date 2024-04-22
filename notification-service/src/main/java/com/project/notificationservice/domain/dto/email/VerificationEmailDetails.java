package com.project.notificationservice.domain.dto.email;

import lombok.Getter;

/**
 * A specific extension of {@link EmailDetails} representing details for a verification email.
 */
@Getter
public class VerificationEmailDetails extends EmailDetails {

  private final String verificationToken;

  /**
   * Constructs a new VerificationEmailDetails instance with the specified details.
   *
   * @param userEmail         The user email recipient.
   * @param verificationToken The url which contain all necessary info to verify email.
   */
  public VerificationEmailDetails(String userEmail, String verificationToken) {
    super(userEmail, EmailType.VERIFICATION_EMAIL);
    this.verificationToken = verificationToken;
  }
}
