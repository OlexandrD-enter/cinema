package com.project.notificationservice.service.impl;

import com.project.notificationservice.domain.dto.email.EmailDetails;
import com.project.notificationservice.domain.dto.email.EmailType;
import com.project.notificationservice.service.EmailService;
import com.project.notificationservice.service.email.EmailMessage;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

/**
 * EmailService implementation responsible for email related operations.
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

  private final String sourceAddress;
  private final JavaMailSender mailSender;
  private final Map<EmailType, EmailMessage> emails;

  /**
   * Constructs an instance of EmailServiceImpl type.
   *
   * @param sourceAddress source address that are going to appear for user who receives a
   *                      verification letter
   * @param mailSender    Java Mail Sender which actually sends letter to the given user.
   * @param emails        representing a mapping between email types and their corresponding message
   *                      instances.
   */
  public EmailServiceImpl(
      @Value("${email.source-address}") String sourceAddress,
      JavaMailSender mailSender,
      Map<EmailType, EmailMessage> emails) {
    this.sourceAddress = sourceAddress;
    this.mailSender = mailSender;
    this.emails = emails;
  }

  @Override
  public void sendEmail(EmailDetails emailDetails) {
    EmailMessage emailMessage = emails.get(emailDetails.getType());
    String body = emailMessage.getBody(emailDetails);

    MimeMessagePreparator mimeMessagePreparator = msg -> {
      MimeMessageHelper helper = new MimeMessageHelper(msg,
          true, "UTF-8");

      helper.setSubject(emailMessage.getSubject());
      helper.setFrom(sourceAddress);
      helper.setTo(emailDetails.getEmail());
      helper.setText(body, true);
    };

    mailSender.send(mimeMessagePreparator);
    log.debug("Email is sent successfully!");
  }
}
