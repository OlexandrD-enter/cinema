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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a Showtime entity storing information about show time of movies in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "showtimes")
@EntityListeners(AuditingEntityListener.class)
public class Showtime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "movie_id")
  private Movie movie;

  @ManyToOne
  @JoinColumn(name = "room_id")
  private CinemaRoom cinemaRoom;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(name = "price")
  private BigDecimal price;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "showtime", cascade = CascadeType.ALL,
      orphanRemoval = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<Ticket> tickets = new ArrayList<>();

  @Embedded
  @Builder.Default
  private AuditEntity auditEntity = new AuditEntity();
}
