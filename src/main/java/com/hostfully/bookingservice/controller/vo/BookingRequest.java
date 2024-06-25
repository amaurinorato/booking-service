package com.hostfully.bookingservice.controller.vo;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequest {

  @NotNull private LocalDate start;
  @NotNull private LocalDate end;
  private Long guestId;
  private Long ownerId;
  @NotNull private Long propertyId;
}
