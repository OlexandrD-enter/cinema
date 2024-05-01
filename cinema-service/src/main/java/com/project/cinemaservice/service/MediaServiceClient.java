package com.project.cinemaservice.service;

import com.project.cinemaservice.domain.dto.movie.MovieFileRequest;
import com.project.cinemaservice.domain.dto.movie.MovieFileResponse;
import com.project.cinemaservice.service.exception.FileConvertException;
import com.project.cinemaservice.service.exception.MediaServiceException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 * Service class responsible for interacting with the media service.
 */
@Service
@RequiredArgsConstructor
public class MediaServiceClient {

  private final WebClient mediaWebClient;

  /**
   * Uploads a file to the media service.
   *
   * @param movieFileRequest The request containing information about the file
   * @param file             The file to upload
   * @return The response from the media service
   * @throws MediaServiceException If an exception occurs during the media service call
   */
  public MovieFileResponse uploadFile(MovieFileRequest movieFileRequest, MultipartFile file) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    body.add("request", movieFileRequest);
    try {
      body.add("file", new ByteArrayResource(file.getBytes()) {
        @Override
        public String getFilename() {
          return file.getOriginalFilename();
        }
      });
    } catch (IOException e) {
      throw new FileConvertException("Exception when converting file to upload");
    }

    return mediaWebClient.post()
        .uri("/api/v1/admin/files")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(body))
        .retrieve()
        .bodyToMono(MovieFileResponse.class)
        .onErrorMap(WebClientRequestException.class,
            e -> new MediaServiceException("Media service call exception"))
        .blockOptional()
        .orElseThrow(() -> new MediaServiceException("Media service call exception"));
  }

  /**
   * Deletes a file from the media service by its ID.
   *
   * @param fileId The ID of the file to delete
   * @throws MediaServiceException If an exception occurs during the media service call
   */
  public void deleteFileById(Long fileId) {
    mediaWebClient.delete()
        .uri("/api/v1/admin/files/{fileId}", fileId)
        .retrieve()
        .bodyToMono(Void.class)
        .onErrorMap(WebClientRequestException.class,
            e -> new MediaServiceException("Media service call exception"))
        .block();
  }
}
