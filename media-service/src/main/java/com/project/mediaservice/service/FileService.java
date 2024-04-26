package com.project.mediaservice.service;

import com.project.mediaservice.domain.dto.FileRequest;
import com.project.mediaservice.domain.dto.FileResponse;
import com.project.mediaservice.persistence.model.FileEntity;
import java.util.List;

/**
 * FileService interface for operations with files.
 */
public interface FileService {

  List<FileEntity> save(List<FileRequest> fileRequest);

  void deleteById(Long id);

  FileResponse getById(Long id);
}
