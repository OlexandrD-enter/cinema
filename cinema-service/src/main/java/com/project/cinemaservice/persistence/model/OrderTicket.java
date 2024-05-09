package com.project.cinemaservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a OrderTicket entity storing information about order tickets in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_tickets")
@EntityListeners(AuditingEntityListener.class)
public class OrderTicket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne
  @JoinColumn(name = "ticket_id")
  private Ticket ticket;

  @Embedded
  @Builder.Default
  private AuditEntity auditEntity = new AuditEntity();
}
