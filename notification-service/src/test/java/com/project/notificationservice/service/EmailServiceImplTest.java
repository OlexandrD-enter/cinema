package com.project.notificationservice.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.notificationservice.domain.dto.email.EmailDetails;
import com.project.notificationservice.domain.dto.email.EmailType;
import com.project.notificationservice.domain.dto.email.VerificationEmailDetails;
import com.project.notificationservice.service.email.EmailMessage;
import com.project.notificationservice.service.impl.EmailServiceImpl;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTest {

  @Mock
  private JavaMailSender mailSender;
  @Mock
  private Map<EmailType, EmailMessage> emails;

  private EmailServiceImpl emailService;

  @BeforeEach
  public void openMocks() {
    emailService = new EmailServiceImpl("source@cinema.software",
        mailSender, emails);
  }

  @Test
  public void testSendEmail() throws Exception {
    // Given
    EmailDetails emailDetails = new VerificationEmailDetails("recepient@cinema.software",
        "verificationUrl");

    EmailMessage emailMessage = Mockito.mock(EmailMessage.class);
    when(emails.get(emailDetails.getType())).thenReturn(emailMessage);
    String expectedBody = "Some body";
    when(emailMessage.getBody(emailDetails)).thenReturn(expectedBody);
    String expectedSubject = "Registration success";
    when(emailMessage.getSubject()).thenReturn(expectedSubject);

    ArgumentCaptor<MimeMessagePreparator> captor = ArgumentCaptor.forClass(
        MimeMessagePreparator.class);
    doNothing().when(mailSender).send(captor.capture());

    //When
    emailService.sendEmail(emailDetails);

    MimeMessagePreparator capturedPreparator = captor.getValue();
    MimeMessage mockMimeMessage = Mockito.mock(MimeMessage.class);
    capturedPreparator.prepare(mockMimeMessage);

    //Then
    verify(mailSender, times(1)).send(captor.capture());
    verify(mockMimeMessage).setSubject(expectedSubject, "UTF-8");
  }
}
