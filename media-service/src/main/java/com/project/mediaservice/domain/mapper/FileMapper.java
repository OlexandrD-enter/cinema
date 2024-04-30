package com.project.mediaservice.domain.mapper;

import com.project.mediaservice.domain.dto.FileRequest;
import com.project.mediaservice.domain.dto.FileResponse;
import com.project.mediaservice.domain.dto.FileToS3Upload;
import com.project.mediaservice.persistence.model.FileEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

/**
 * Mapper interface for file operations.
 */
@Mapper(componentModel = "spring")
public interface FileMapper {

  /**
   * Maps a FileRequest and a name to a FileEntity.
   *
   * @param fileRequest The FileRequest object
   * @param name        The name for the file
   * @return FileEntity generated from the FileRequest and name
   */
  FileEntity toEntity(FileRequest fileRequest, String name);

  /**
   * Maps a FileEntity and an access URL to a FileResponse.
   *
   * @param fileEntity The FileEntity object
   * @param accessUrl  The access URL for the file
   * @return FileResponse generated from FileEntity and access URL
   */
  FileResponse toResponse(FileEntity fileEntity, String accessUrl);

  /**
   * Converts a FileRequest to a list of FileToS3Upload objects.
   *
   * @return List of FileToS3Upload objects generated from FileRequest
   */
  default FileToS3Upload toS3File(FileRequest fileRequest, MultipartFile multipartFile) {
    return new FileToS3Upload(generateFileName(fileRequest), multipartFile);
  }

  /**
   * Generates a file name for a multipart file based on FileRequest data.
   *
   * @param fileRequest The FileRequest object
   * @return String representing the generated file name
   */
  default String generateFileName(FileRequest fileRequest) {
    String fileNamePrefix = fileRequest.getTargetType().getPrefixForId(fileRequest.getTargetId());
    return fileNamePrefix + UUID.randomUUID() + ".%s"
        .formatted(fileRequest.getMimeType().getSubtype());
  }
}

