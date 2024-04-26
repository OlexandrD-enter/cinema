package com.project.mediaservice.domain.dto;

import com.project.mediaservice.persistence.enums.TargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents a request for creating a new file entity.
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileRequest {

  @Schema(example = "1")
  @NotNull(message = "Not valid targetId")
  @Min(value = 1, message = "targetId must be greater than 0")
  private Long targetId;

  @Schema(example = "MOVIE_PREVIEW")
  @NotNull(message = "Not valid targetType")
  @Pattern(regexp = "^(MOVIE_PREVIEW|MOVIE_TRAILER)$",
      message = "Target type must be either MOVIE_PREVIEW or MOVIE_TRAILER")
  private TargetType targetType;

  @Schema(example = "image/jpeg")
  @NotNull(message = "Not valid mimeType")
  private MimeType mimeType;

  @NotNull(message = "Not valid file")
  MultipartFile file;
}
