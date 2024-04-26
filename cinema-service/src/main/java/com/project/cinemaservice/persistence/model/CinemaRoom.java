package com.project.cinemaservice.persistence.model;

import com.project.cinemaservice.persistence.enums.RoomType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a CinemaRoom entity storing cinema rooms information in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cinema_rooms",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"cinema_id", "name"})})
@EntityListeners(AuditingEntityListener.class)
public class CinemaRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "room_type")
  @Enumerated(EnumType.STRING)
  private RoomType roomType;

  @ManyToOne
  @JoinColumn(name = "cinema_id")
  private Cinema cinema;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "cinemaRoom", cascade = CascadeType.ALL)
  private List<RoomSeat> roomSeats;

  @Embedded
  @Builder.Default
  private AuditEntity auditEntity = new AuditEntity();
}
