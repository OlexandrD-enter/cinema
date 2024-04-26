package com.project.mediaservice.persistence.model;

import com.project.mediaservice.persistence.enums.TargetType;
import com.project.mediaservice.util.MimeTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.MimeType;

/**
 * Represents a file entity.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "files")
@EntityListeners(AuditingEntityListener.class)
public class FileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", unique = true)
  private String name;

  @Column(name = "target_id")
  private Long targetId;

  @Column(name = "target_type")
  @Enumerated(EnumType.STRING)
  private TargetType targetType;

  @Column(name = "mime_type")
  @Convert(converter = MimeTypeConverter.class)
  private MimeType mimeType;

  @Embedded
  @Builder.Default
  private AuditEntity auditEntity = new AuditEntity();
}
