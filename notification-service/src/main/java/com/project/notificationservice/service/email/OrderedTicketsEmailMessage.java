package com.project.notificationservice.service.email;

import static com.project.notificationservice.domain.dto.email.EmailType.ORDERED_TICKETS_EMAIL;

import com.project.notificationservice.domain.dto.email.EmailDetails;
import com.project.notificationservice.domain.dto.email.OrderedTicketsDetails;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Represents construction of an email message for order paid tickets. Extends the EmailMessage
 * class.
 */
@Component
public class OrderedTicketsEmailMessage extends EmailMessage {

  public static final String SUBJECT_ORDERED_TICKETS_EMAIL = "Ordered tickets. Cinema service.";

  @Autowired
  public OrderedTicketsEmailMessage(SpringTemplateEngine templateEngine) {
    super(templateEngine);

  }

  @Override
  public String getBody(EmailDetails emailDetails) {
    if (!(emailDetails instanceof OrderedTicketsDetails orderedTicketsDetails)) {
      throw new IllegalArgumentException("Wrong type of the message!");
    }

    String fileUrl = orderedTicketsDetails.getOrderedTickets().getMoviePreviewUrl();

    String pattern = "d MMMM yyyy, HH:mm";
    Locale locale = new Locale("uk", "UA");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
    String startDate = orderedTicketsDetails.getOrderedTickets().getStartDate().format(formatter);

    Map<String, Object> variables = Map.of(
        "email", orderedTicketsDetails.getEmail(),
        "order", orderedTicketsDetails.getOrderedTickets(),
        "startDate", startDate,
        "image", fileUrl
    );
    Context context = new Context();
    context.setVariables(variables);

    return springTemplateEngine.process(ORDERED_TICKETS_EMAIL.getFileName(), context);
  }

  @Override
  public String getSubject() {
    return SUBJECT_ORDERED_TICKETS_EMAIL;
  }

}
