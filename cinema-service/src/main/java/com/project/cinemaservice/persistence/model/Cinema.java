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
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a Cinema entity storing cinema information in the database.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cinemas")
@EntityListeners(AuditingEntityListener.class)
public class Cinema {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", unique = true)
  private String name;

  @Column(name = "city")
  private String city;

  @Column(name = "street_address")
  private String streetAddress;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "cinema", cascade = CascadeType.ALL)
  private List<CinemaRoom> cinemaRooms;

  @Embedded
  @Builder.Default
  private AuditEntity auditEntity = new AuditEntity();
}
