package com.project.notificationservice.service.email;

import com.project.notificationservice.domain.dto.email.EmailDetails;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * An abstract base class representing an email message construction.
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Component
public abstract class EmailMessage {

  protected SpringTemplateEngine springTemplateEngine;

  public abstract String getBody(EmailDetails emailDetails);

  public abstract String getSubject();
}
