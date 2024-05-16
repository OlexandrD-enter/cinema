package com.project.notificationservice.config.email;

import com.project.notificationservice.domain.dto.email.EmailType;
import com.project.notificationservice.service.email.EmailMessage;
import com.project.notificationservice.service.email.OrderedTicketsEmailMessage;
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
   * Provides a map of email messages based on their types.
   *
   * @param verificationEmailMessage Verification email message bean
   * @return Map of email messages keyed by their types
   */
  @Bean
  public Map<EmailType, EmailMessage> emails(
      VerificationEmailMessage verificationEmailMessage,
      OrderedTicketsEmailMessage orderedTicketsEmailMessage
  ) {
    return Map.of(
        EmailType.VERIFICATION_EMAIL, verificationEmailMessage,
        EmailType.ORDERED_TICKETS_EMAIL, orderedTicketsEmailMessage
    );
  }
}
