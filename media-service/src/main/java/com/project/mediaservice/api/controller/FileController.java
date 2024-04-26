package com.project.mediaservice.api.controller;

import com.project.mediaservice.domain.dto.FileRequest;
import com.project.mediaservice.domain.dto.FileResponse;
import com.project.mediaservice.persistence.model.FileEntity;
import com.project.mediaservice.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to files.<br> Endpoints provided:<br>
 * - POST /: Creates a new file based on request data.<br>
 * - GET /{fileId}: Gets a file.<br>
 * - DELETE /{fileId}: Delete a file based on request data.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/files")
public class FileController {

  private final FileService fileService;

  @Operation(summary = "This method is used for file entity creation.")
  @PostMapping
  public ResponseEntity<List<FileEntity>> createFileEntity(
      @ModelAttribute @Valid List<FileRequest> fileRequestList) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(fileService.save(fileRequestList));
  }

  @Operation(summary = "This method retrieves a file details.")
  @GetMapping("/{fileId}")
  public ResponseEntity<FileResponse> getFile(@PathVariable @Min(1) Long fileId) {
    return ResponseEntity.ok(fileService.getById(fileId));
  }

  @Operation(summary = "This method is used to delete the file.")
  @DeleteMapping("/{fileId}")
  public ResponseEntity<HttpStatus> deleteFile(@PathVariable @Min(1) Long fileId) {
    fileService.deleteById(fileId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
