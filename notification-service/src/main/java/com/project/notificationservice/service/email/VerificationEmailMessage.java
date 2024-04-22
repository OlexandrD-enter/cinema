package com.project.notificationservice.service.email;

import static com.project.notificationservice.domain.dto.email.EmailType.VERIFICATION_EMAIL;

import com.project.notificationservice.domain.dto.email.EmailDetails;
import com.project.notificationservice.domain.dto.email.VerificationEmailDetails;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Represents construction of an email message for user verification. Extends the
 * {@link EmailMessage} class.
 */
@Component
public class VerificationEmailMessage extends EmailMessage {

  public static final String SUBJECT_VERIFICATION_EMAIL = "Your registration for Cinema service!";
  private final String emailVerificationUrl;

  @Autowired
  public VerificationEmailMessage(SpringTemplateEngine templateEngine,
      @Value("${email.verification-url}") String emailVerificationUrl) {
    super(templateEngine);
    this.emailVerificationUrl = emailVerificationUrl;
  }

  @Override
  public String getBody(EmailDetails emailDetails) {
    if (!(emailDetails instanceof VerificationEmailDetails verificationEmailDetails)) {
      throw new IllegalArgumentException("Wrong type of the message!");
    }

    Map<String, Object> variables = Map.of(
        "email", verificationEmailDetails.getEmail(),
        "confirmEmailUrl",
        emailVerificationUrl + "/verify-email/" + verificationEmailDetails.getVerificationToken()
    );
    Context context = new Context();
    context.setVariables(variables);

    return springTemplateEngine.process(VERIFICATION_EMAIL.getFileName(), context);
  }

  @Override
  public String getSubject() {
    return SUBJECT_VERIFICATION_EMAIL;
  }
}
