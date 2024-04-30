package com.project.mediaservice.service;

import com.project.mediaservice.domain.dto.FileRequest;
import com.project.mediaservice.domain.dto.FileResponse;
import com.project.mediaservice.persistence.model.FileEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileService interface for operations with files.
 */
public interface FileService {

  FileEntity save(FileRequest fileRequest, MultipartFile multipartFile);

  void deleteById(Long id);

  FileResponse getById(Long id);
}
