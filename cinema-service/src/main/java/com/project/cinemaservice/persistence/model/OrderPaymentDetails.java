package com.project.cinemaservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_payment_details")
@EntityListeners(AuditingEntityListener.class)
public class OrderPaymentDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "order_id", unique = true)
  private Order order;

  @Column(name = "transaction_id", unique = true)
  private String transactionId;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updatedAt;
}
