package com.project.mediaservice.domain.mapper;

import com.project.mediaservice.domain.dto.FileRequest;
import com.project.mediaservice.domain.dto.FileResponse;
import com.project.mediaservice.domain.dto.FileToS3Upload;
import com.project.mediaservice.persistence.model.FileEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;

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
   * Converts a list of FileToS3Upload objects to a list of FileEntity objects using FileRequest.
   *
   * @param fileNames       List of String which contain names of files
   * @param fileRequestList List of FileRequest
   * @return List of FileEntity objects generated from fileNames and FileRequest objects
   */
  default List<FileEntity> toEntities(List<FileRequest> fileRequestList, List<String> fileNames) {
    List<FileEntity> fileEntities = new ArrayList<>();
    for (int i = 0; i < fileRequestList.size(); i++) {
      fileEntities.add(
          toEntity(fileRequestList.get(i), fileNames.get(i))
      );
    }
    return fileEntities;
  }

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
   * @param fileRequestList List of FileRequest
   * @return List of FileToS3Upload objects generated from FileRequest
   */
  default List<FileToS3Upload> toS3Files(List<FileRequest> fileRequestList) {
    List<FileToS3Upload> fileToS3Uploads = new ArrayList<>();
    for (FileRequest fileRequest : fileRequestList) {
      fileToS3Uploads.add(
          new FileToS3Upload(generateFileName(fileRequest), fileRequest.getFile())
      );
    }
    return fileToS3Uploads;
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

