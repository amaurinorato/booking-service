package com.hostfully.bookingservice.controller.vo;

import com.hostfully.bookingservice.domain.Booking;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingResponse {

  private Long bookingId;
  private LocalDate start;
  private LocalDate end;
  private Long guestId;
  private Long ownerId;
  private Boolean isCanceled;
  private Long propertyId;

  public BookingResponse(Booking booking) {
    this.bookingId = booking.getId();
    this.start = booking.getStart();
    this.end = booking.getEnd();
    if (booking.getGuest() != null) this.guestId = booking.getGuest().getId();
    if (booking.getOwner() != null) this.ownerId = booking.getOwner().getId();
    this.isCanceled = booking.getCanceled();
    this.propertyId = booking.getProperty().getId();
  }
}
