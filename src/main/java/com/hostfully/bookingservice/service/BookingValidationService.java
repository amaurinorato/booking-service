package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.controller.vo.BookingRequest;
import com.hostfully.bookingservice.domain.Booking;
import com.hostfully.bookingservice.repository.BookingRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class BookingValidationService {

  private final BookingRepository bookingRepository;

  public void validateBooking(BookingRequest bookingRequest, Long bookingId) {
    final var currentDate = LocalDate.now();
    final var startDate = bookingRequest.getStart();
    final var endDate = bookingRequest.getEnd();

    validateDates(startDate, endDate, currentDate);
    validateGuestAndOwner(bookingRequest);
    validateBookingOverlap(bookingId, bookingRequest.getPropertyId(), startDate, endDate);
  }

  public void validateBookingOverlap(
      Long bookingId, Long propertyId, LocalDate startDate, LocalDate endDate) {
    final var bookings =
        bookingRepository.findActiveOverlappingBookings(propertyId, startDate, endDate);
    if (!CollectionUtils.isEmpty(bookings)) {
      if (bookings.size() == 1 && bookings.get(0).getId().equals(bookingId)) {
        return;
      }
      throw new IllegalArgumentException("Property is already booked for the selected dates");
    }
  }

  private void validateGuestAndOwner(BookingRequest bookingRequest) {
    final var ownerId = bookingRequest.getOwnerId();
    final var guestId = bookingRequest.getGuestId();
    if (guestId != null && ownerId != null) {
      throw new IllegalArgumentException(
          "Guest and owner cannot be set at the same time. Use GuestId to book a property and OwnerId to block a property.");
    }
    if (guestId == null && ownerId == null) {
      throw new IllegalArgumentException(
          "Guest or owner must be informed. Use GuestId to book a property and OwnerId to block a property.");
    }
  }

  private void validateDates(LocalDate startDate, LocalDate endDate, LocalDate currentDate) {
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date must be before end date");
    }
    if (currentDate.isAfter(startDate)) {
      throw new IllegalArgumentException("Start date must be in the future");
    }
    if (currentDate.isAfter(endDate)) {
      throw new IllegalArgumentException("End date must be in the future");
    }
  }

  public void validateBlocking(Booking booking) {
    if (!booking.getProperty().getOwner().equals(booking.getOwner())) {
      throw new IllegalArgumentException("Owner can only block his own property");
    }
  }
}
