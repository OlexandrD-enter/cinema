package com.project.cinemaservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.project.cinemaservice.domain.dto.movie.MovieFileRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponse;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponseUrl;
import com.project.cinemaservice.service.exception.MediaServiceException;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

class MediaServiceClientTest {

  private MockWebServer mockWebServer;
  private MediaServiceClient mediaServiceClient;

  @BeforeEach
  void setUp() throws Exception {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient webClient = WebClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    mediaServiceClient = new MediaServiceClient(webClient);
  }

  @AfterEach
  void tearDown() throws Exception {
    mockWebServer.shutdown();
  }

  @SneakyThrows
  @Test
  void uploadFile_Success() {
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setBody("{\"id\": 123, \"name\": \"test.jpg\"}")
        .setHeader("Content-Type", "application/json"));

    MovieFileRequest movieFileRequest = new MovieFileRequest();
    MultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
        "test data".getBytes());

    MovieFileResponse response = mediaServiceClient.uploadFile(movieFileRequest, file);

    assertEquals(123, response.getId());
    assertEquals("test.jpg", response.getName());

    RecordedRequest request = mockWebServer.takeRequest();
    assertEquals("/api/v1/files", request.getPath());
    assertEquals("POST", request.getMethod());
    assertNotNull(request.getBody());
  }

  @SneakyThrows
  @Test
  void uploadFile_Error() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    MovieFileRequest movieFileRequest = new MovieFileRequest();
    MultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
        "test data".getBytes());

    assertThrows(MediaServiceException.class,
        () -> mediaServiceClient.uploadFile(movieFileRequest, file));
  }

  @SneakyThrows
  @Test
  void deleteFileById_Success() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(204));

    mediaServiceClient.deleteFileById(123L);

    RecordedRequest request = mockWebServer.takeRequest();
    assertEquals("/api/v1/files/123", request.getPath());
    assertEquals("DELETE", request.getMethod());
  }

  @Test
  void deleteFileById_Error() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(MediaServiceException.class,
        () -> mediaServiceClient.deleteFileById(123L));
  }

  @Test
  void getFile_Success() {
    mockWebServer.enqueue(new MockResponse()
        .setResponseCode(200)
        .setBody(
            "{\"id\": 123, \"name\": \"test.jpg\", \"accessUrl\": \"http://example.com/test.jpg\"}")
        .setHeader("Content-Type", "application/json"));

    MovieFileResponseUrl response = mediaServiceClient.getFile(123L);

    assertEquals(123, response.getId());
    assertEquals("test.jpg", response.getName());
    assertEquals("http://example.com/test.jpg", response.getAccessUrl());
  }

  @Test
  void getFile_Error() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(MediaServiceException.class,
        () -> mediaServiceClient.getFile(123L));
  }
}

