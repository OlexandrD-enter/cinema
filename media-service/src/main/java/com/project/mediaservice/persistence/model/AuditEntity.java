package com.project.mediaservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Embeddable class for audit-related fields.
 */
@Embeddable
@Getter
@Setter
public class AuditEntity {

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @LastModifiedDate
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
  private LocalDateTime updatedAt;

  @Column(name = "created_by_email", updatable = false)
  @CreatedBy
  private String createdBy;

  @Column(name = "modified_by_email")
  @LastModifiedBy
  private String modifiedBy;
}
