package com.project.mediaservice.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for Amazon S3.
 * This class sets up the Amazon S3 client using the provided credentials and region.
 */
@Profile("!test")
@Configuration
public class AmazonS3Config {

  @Value("${aws.s3.access-key}")
  private String accessKey;

  @Value("${aws.s3.secret-key}")
  private String secretKey;

  @Value("${aws.s3.region}")
  private String region;

  /**
   * Bean definition for Amazon S3 client.
   *
   * @return AmazonS3 - Amazon S3 client instance
   */
  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials()))
        .withRegion(region)
        .build();
  }

  private AWSCredentials credentials() {
    return new BasicAWSCredentials(accessKey, secretKey);
  }
}
