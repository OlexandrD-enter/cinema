package com.project.mediaservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents a file to S3 uploading.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileToS3Upload {

  String name;
  MultipartFile file;
}
