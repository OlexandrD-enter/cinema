package com.project.notificationservice.config.email;

import com.project.notificationservice.domain.dto.email.EmailType;
import com.project.notificationservice.service.email.EmailMessage;
import com.project.notificationservice.service.email.VerificationEmailMessage;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Email-related configuration class.
 */
@Configuration
public class EmailConfiguration {

  /**
   * Creates a bean representing a mapping between email types and their corresponding message
   * instances.
   *
   * @return A {@link Map} containing the mapping between {@link EmailType} and
   * {@link EmailMessage}.
   */
  @Bean
  public Map<EmailType, EmailMessage> emails(
      VerificationEmailMessage verificationEmailMessage
  ) {
    return Map.of(
        EmailType.VERIFICATION_EMAIL, verificationEmailMessage
    );
  }
}
