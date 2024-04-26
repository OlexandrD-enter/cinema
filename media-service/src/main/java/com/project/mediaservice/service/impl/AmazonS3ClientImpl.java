package com.project.mediaservice.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.project.mediaservice.domain.dto.FileToS3Upload;
import com.project.mediaservice.service.AmazonS3Client;
import com.project.mediaservice.service.exception.FileUploadException;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * AmazonS3Client implementation responsible for operations with bucket.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3ClientImpl implements AmazonS3Client {

  @Value("${aws.s3.bucket}")
  private String bucket;
  @Value("${aws.s3.link-expiration}")
  private Integer expiration;
  private final AmazonS3 amazonS3;

  @Override
  public void uploadFileToBucket(FileToS3Upload fileToS3Upload) {
    try {
      ObjectMetadata meta = new ObjectMetadata();
      meta.setContentLength(fileToS3Upload.getFile().getInputStream().available());
      meta.setContentType(fileToS3Upload.getFile().getContentType());
      PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileToS3Upload.getName(),
          fileToS3Upload.getFile().getInputStream(), meta);
      log.debug("Uploading file %s to bucket".formatted(fileToS3Upload.getName()));
      amazonS3.putObject(putObjectRequest);
      log.debug("File %s uploaded to bucket".formatted(fileToS3Upload.getName()));
    } catch (IOException e) {
      throw new FileUploadException("Failed to upload file to S3 bucket");
    }
  }

  @Override
  public void deleteFileFromBucket(String filename) {
    DeleteObjectRequest delObjReq = new DeleteObjectRequest(bucket, filename);
    log.debug("Deleting file %s from bucket".formatted(filename));
    amazonS3.deleteObject(delObjReq);
    log.debug("File %s deleted from bucket".formatted(filename));
  }

  @Override
  public String generateGetPreSignedUrl(String fileName) {
    if (amazonS3.doesBucketExistV2(bucket)) {
      LocalDateTime time = LocalDateTime.now().plusMinutes(expiration);
      GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
          .withMethod(HttpMethod.GET)
          .withExpiration(Timestamp.valueOf(time));
      log.debug("Generating presigned url for %s".formatted(HttpMethod.GET.name()));
      URL url = amazonS3.generatePresignedUrl(request);
      log.debug("Generation of presigned url is successfully fulfilled %n url =%s"
          .formatted(url.toString()));
      return url.toString();
    } else {
      throw new EntityNotFoundException(
          "The bucket doesn't exist, create a bucket before generating access link");
    }
  }
}
