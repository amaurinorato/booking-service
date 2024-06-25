package com.hostfully.bookingservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "start_date", nullable = false)
  private LocalDate start;

  @Column(name = "end_date", nullable = false)
  private LocalDate end;

  private Boolean canceled;

  @ManyToOne
  @JoinColumn(name = "guest_id")
  private Guest guest;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  private Owner owner;

  @ManyToOne
  @JoinColumn(name = "property_id", nullable = false)
  private Property property;

  public boolean isBlocking() {
    return owner != null;
  }
}
