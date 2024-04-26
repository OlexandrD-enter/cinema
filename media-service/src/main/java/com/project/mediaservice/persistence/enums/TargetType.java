package com.project.mediaservice.persistence.enums;

/**
 * Enum class that represents types of images by entities.
 */
public enum TargetType {

  MOVIE_PREVIEW("movie-preview/%d/"),
  MOVIE_TRAILER("movie-trailer/%d/");

  private final String folderPrefix;

  TargetType(String folderPrefix) {
    this.folderPrefix = folderPrefix;
  }

  public String getPrefixForId(Long id) {
    return folderPrefix.formatted(id);
  }
}
