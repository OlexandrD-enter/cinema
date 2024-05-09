package com.project.cinemaservice.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a RoomSeat entity storing room seats information in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "room_seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"seat_number", "room_id"})})
@EntityListeners(AuditingEntityListener.class)
public class RoomSeat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "seat_number")
  private Long seatNumber;

  @ManyToOne
  @JoinColumn(name = "room_id")
  private CinemaRoom cinemaRoom;

  @Embedded
  @Builder.Default
  private AuditEntity auditEntity = new AuditEntity();
}
