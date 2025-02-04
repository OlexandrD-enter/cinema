package com.project.cinemaservice.domain.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents a request for create movie.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDataRequest {

  @Schema(example = "STALKER 2")
  @NotNull(message = "Not valid name")
  @Pattern(regexp = "^[a-zA-Z0-9,./ ]{2,50}$",
      message = "Not valid name, length [2,50]")
  private String name;
  @Schema(example = "Short description of movie")
  @NotNull(message = "Not valid description")
  @Pattern(regexp = "^[a-zA-Z0-9,./ ]{2,255}$",
      message = "Not valid description, only letters and nums, length [2,50]")
  private String description;
  @Schema(example = "12")
  @NotNull(message = "Not valid ageLimit")
  @Min(1)
  private Integer ageLimit;
  @Schema(example = "Ukrainian")
  @NotNull(message = "Not valid language")
  @Pattern(regexp = "^[a-zA-Z ]{2,50}$",
      message = "Not valid language, only letters, length [2,50]")
  private String language;
  @Schema(example = "Ukraine")
  @NotNull(message = "Not valid country")
  @Pattern(regexp = "^[a-zA-Z ]{2,50}$",
      message = "Not valid country, only letters, length [2,50]")
  private String country;
  @Schema(example = "Oleksandr Demchenko")
  @NotNull(message = "Not valid director")
  @Pattern(regexp = "^[a-zA-Z ]{2,50}$",
      message = "Not valid director, only letters, length [2,50]")
  private String director;
  @Schema(example = "2024-04-30T15:25:00")
  @NotNull(message = "Not valid realiseDate, YYYY-MM-DDTHH:MM:SS")
  private LocalDateTime realiseDate;
  @NotNull(message = "Not valid duration")
  private Duration duration;
  @Schema(example = "[1, 2, 3]")
  @NotNull(message = "Not valid movieGenreIds")
  private List<Long> movieGenreIds;
  @Schema(example = "previewImage.jpeg")
  @NotNull(message = "Not valid previewImage")
  private MultipartFile previewImage;
  @Schema(example = "trailerVideo.mp4")
  @NotNull(message = "Not valid trailerVideo")
  private MultipartFile trailerVideo;
}
