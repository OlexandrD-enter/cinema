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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public List<FileEntity> save(List<FileRequest> fileRequestList) {
    List<FileToS3Upload> filesToS3Upload = fileMapper.toS3Files(fileRequestList);
    List<String> uploadedToAwsFilesNames = filesToS3Upload.stream()
        .map(FileToS3Upload::getName)
        .toList();
    List<FileEntity> files = fileMapper.toEntities(fileRequestList, uploadedToAwsFilesNames);

    log.debug("Trying to save new files into db");
    fileRepository.saveAll(files);
    log.debug("Files are saved to database");

    log.debug("Trying to save new files into bucket");
    filesToS3Upload.forEach(amazonS3Client::uploadFileToBucket);
    log.debug("Files are saved to bucket");

    return files;
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
