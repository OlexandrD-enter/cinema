package com.project.notificationservice.listener;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.notificationservice.domain.dto.email.VerificationEmailDetails;
import com.project.notificationservice.domain.dto.user.UserEmailVerification;
import com.project.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RegistrationListenerTest {

  @Mock
  private EmailService emailService;
  @InjectMocks
  private RegistrationListener registrationListener;

  @Test
  void listener_EmailVerificationMessage_EmailSent() {
    // Given
    UserEmailVerification userEmailVerification = new UserEmailVerification(
        "recepient@cinema.software",
        "verificationToken");

    // When
    registrationListener.registrationListener(userEmailVerification);

    // Then
    verify(emailService, times(1)).sendEmail(any(VerificationEmailDetails.class));
  }
}

