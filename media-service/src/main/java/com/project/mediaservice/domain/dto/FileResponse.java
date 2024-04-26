package com.project.mediaservice.domain.dto;

import com.project.mediaservice.persistence.enums.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.MimeType;

/**
 * Represents a response of file entity.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileResponse {

  private Long id;
  private String name;
  private Long targetId;
  private TargetType targetType;
  private String mimeType;
  private String accessUrl;

  public void setMimeType(MimeType mimeType) {
    this.mimeType = mimeType.toString();
  }
}