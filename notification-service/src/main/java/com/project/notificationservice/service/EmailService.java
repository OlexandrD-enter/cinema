package com.project.notificationservice.service;

import com.project.notificationservice.domain.dto.email.EmailDetails;

/**
 * EmailService interface for email related operations.
 */
public interface EmailService {

  void sendEmail(EmailDetails emailDetails);
}
