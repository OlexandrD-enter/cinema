package com.project.mediaservice.service.impl;

import com.project.mediaservice.domain.dto.FileRequest;
import com.project.mediaservice.domain.dto.FileResponse;
import com.project.mediaservice.domain.dto.FileToS3Upload;
import com.project.mediaservice.domain.mapper.FileMapper;
import com.project.mediaservice.persistence.model.FileEntity;
import com.project.mediaservice.persistence.repository.FileRepository;
import com.project.mediaservice.service.AmazonS3Client;
import com.project.mediaservice.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileService implementation responsible for operations with files.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;
  private final AmazonS3Client amazonS3Client;
  private final FileMapper fileMapper;

  @Transactional
  @Override
  public FileEntity save(FileRequest fileRequest, MultipartFile multipartFile) {
    FileToS3Upload filesToS3Upload = fileMapper.toS3File(fileRequest, multipartFile);
    String uploadedToAwsFilesName = filesToS3Upload.getName();

    FileEntity file = fileMapper.toEntity(fileRequest, uploadedToAwsFilesName);

    log.debug("Trying to save new files into db");
    fileRepository.save(file);
    log.debug("Files are saved to database");

    log.debug("Trying to save new files into bucket");
    amazonS3Client.uploadFileToBucket(filesToS3Upload);
    log.debug("Files are saved to bucket");

    return file;
  }

  @Transactional
  @Override
  public void deleteById(Long id) {
    String fileName = fileRepository.getFileNameById(id);
    if (fileName != null) {
      log.debug("Trying to delete file with id %d".formatted(id));
      amazonS3Client.deleteFileFromBucket(fileName);
      fileRepository.deleteById(id);
      log.debug("File with id %d successfully deleted".formatted(id));
    } else {
      throw new EntityNotFoundException("File with id %d is not found".formatted(id));
    }
  }

  @Transactional(readOnly = true)
  @Override
  public FileResponse getById(Long id) {
    FileEntity fileEntity = fileRepository.findById(id).orElseThrow(() ->
        new EntityNotFoundException("File with id '%d' not found".formatted(id)));

    String accessUrl = amazonS3Client.generateGetPreSignedUrl(fileEntity.getName());

    return fileMapper.toResponse(fileEntity, accessUrl);
  }
}
