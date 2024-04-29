package com.project.cinemaservice.api.admin;

import com.project.cinemaservice.domain.dto.genre.GenreAdminBriefResponse;
import com.project.cinemaservice.domain.dto.genre.GenreAdminResponse;
import com.project.cinemaservice.domain.dto.genre.GenreDataRequest;
import com.project.cinemaservice.service.GenreService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling operations related to genres.<br> Endpoints provided:<br>
 * - POST /: Creates a new genre based on request data.<br>
 * - PUT /{genreSeatId}: Updates a genre based on request data.<br>
 * - GET /genreSeatId: Gets a genre for admin.<br>
 * - GET /: Gets a genres for admin.<br>
 * - DELETE /{genreId}: Deletes a genre based on request data.<br>
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/genres")
public class GenreController {

  private final GenreService genreService;

  @Operation(summary = "This method is used for genre creation.")
  @PostMapping
  public ResponseEntity<GenreAdminResponse> createGenre(
      @RequestBody @Valid GenreDataRequest genreDataRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(genreService.createCinema(genreDataRequest));
  }

  @Operation(summary = "This method is used to edit the genre.")
  @PutMapping("/{genreId}")
  public ResponseEntity<GenreAdminResponse> editRoomSeat(
      @PathVariable Long genreId,
      @RequestBody @Valid GenreDataRequest genreDataRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(genreService.editCinema(genreId, genreDataRequest));
  }

  @Operation(summary = "This method retrieves a genre details.")
  @GetMapping("/{genreId}")
  public ResponseEntity<GenreAdminResponse> getGenre(
      @PathVariable @Min(1) Long genreId) {
    return ResponseEntity.ok(genreService.getGenreById(genreId));
  }

  @Operation(summary = "This method retrieves a genres details.")
  @GetMapping
  public ResponseEntity<List<GenreAdminBriefResponse>> getGenres() {
    return ResponseEntity.ok(genreService.getGenres());
  }

  @Operation(summary = "This method is used to delete the genre.")
  @DeleteMapping("/{genreId}")
  public ResponseEntity<HttpStatus> deleteGenre(@PathVariable @Min(1) Long genreId) {
    genreService.deleteGenreById(genreId);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
