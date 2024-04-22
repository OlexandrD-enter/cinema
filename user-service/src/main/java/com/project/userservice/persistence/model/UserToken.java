package com.project.userservice.persistence.model;

import com.project.userservice.persistence.enums.TokenType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Represents a UserActionToken entity storing user tokens information in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_tokens")
@EntityListeners(AuditingEntityListener.class)
public class UserToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "token")
  private String token;

  @Column(name = "token_type")
  @Enumerated(value = EnumType.STRING)
  private TokenType tokenType;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "is_used")
  private Boolean isUsed;

  @Column(name = "last_send_at")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime lastSendAt;

  @Embedded
  private AuditEntity auditEntity;
}