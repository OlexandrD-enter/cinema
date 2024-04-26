package com.project.cinemaservice.persistence.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "movies")
@EntityListeners(AuditingEntityListener.class)
public class Movie {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", unique = true)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "age_limit")
  private Integer ageLimit;

  @Column(name = "language")
  private String language;

  @Column(name = "country")
  private String country;

  @Column(name = "director")
  private String director;

  @Column(name = "realise_date")
  private LocalDateTime realiseDate;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie", cascade = CascadeType.ALL)
  private List<MovieGenre> movieGenres;

  @Embedded
  @Builder.Default
  private AuditEntity auditEntity = new AuditEntity();
}
