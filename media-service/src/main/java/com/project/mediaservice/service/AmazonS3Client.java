package com.project.mediaservice.service;

import com.project.mediaservice.domain.dto.FileToS3Upload;

/**
 * AmazonS3Client interface for operations with bucket.
 */
public interface AmazonS3Client {

  void uploadFileToBucket(FileToS3Upload fileToS3Upload);

  void deleteFileFromBucket(String filename);

  String generateGetPreSignedUrl(String fileName);

}
