package com.hostfully.bookingservice.service;

import com.hostfully.bookingservice.controller.vo.BookingPatchStatus;
import com.hostfully.bookingservice.controller.vo.BookingRequest;
import com.hostfully.bookingservice.controller.vo.BookingResponse;
import com.hostfully.bookingservice.domain.Booking;
import com.hostfully.bookingservice.domain.Guest;
import com.hostfully.bookingservice.domain.Owner;
import com.hostfully.bookingservice.domain.Property;
import com.hostfully.bookingservice.repository.BookingRepository;
import com.hostfully.bookingservice.repository.GuestRepository;
import com.hostfully.bookingservice.repository.OwnerRepository;
import com.hostfully.bookingservice.repository.PropertyRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingRepository bookingRepository;
  private final PropertyRepository propertyRepository;
  private final GuestRepository guestRepository;
  private final OwnerRepository ownerRepository;
  private final BookingValidationService bookingValidationService;

  public BookingResponse createBooking(BookingRequest bookingRequest) {
    bookingValidationService.validateBooking(bookingRequest, null);
    return createBooking(bookingRequest, new Booking());
  }

  public BookingResponse updateBooking(BookingRequest bookingRequest, Long id) {
    final var activeBooking = findActiveBookingById(id);
    bookingValidationService.validateBooking(bookingRequest, id);
    return createBooking(bookingRequest, activeBooking);
  }

  public BookingResponse getBooking(Long id) {
    final Booking booking = findBookingById(id);
    return new BookingResponse(booking);
  }

  public void deleteBooking(Long id) {
    bookingRepository.delete(findBookingById(id));
  }

  public void patchBooking(Long id, BookingPatchStatus bookingPatchStatus) {
    final Booking booking = findBookingById(id);

    if (Boolean.TRUE.equals(bookingPatchStatus.getCancel())) {
      booking.setCanceled(true);
    } else {
      bookingValidationService.validateBookingOverlap(
          id, booking.getProperty().getId(), booking.getStart(), booking.getEnd());
      booking.setCanceled(false);
    }
    bookingRepository.save(booking);
  }

  private BookingResponse createBooking(BookingRequest bookingRequest, Booking booking) {
    setOwnerOrGuest(bookingRequest, booking);
    booking.setProperty(getProperty(bookingRequest.getPropertyId()));

    if (booking.isBlocking()) {
      bookingValidationService.validateBlocking(booking);
    }

    booking.setStart(bookingRequest.getStart());
    booking.setEnd(bookingRequest.getEnd());
    booking.setCanceled(false);

    bookingRepository.save(booking);

    return new BookingResponse(booking);
  }

  private void setOwnerOrGuest(BookingRequest bookingRequest, Booking booking) {
    if (bookingRequest.getOwnerId() != null) {
      booking.setOwner(getOwner(bookingRequest.getOwnerId()));
      booking.setGuest(null);
    } else {
      booking.setGuest(getGuest(bookingRequest.getGuestId()));
      booking.setOwner(null);
    }
  }

  private Guest getGuest(Long guestId) {
    return guestRepository
        .findById(guestId)
        .orElseThrow(() -> new NoSuchElementException("Guest not found with id: " + guestId));
  }

  private Owner getOwner(Long ownerId) {
    return ownerRepository
        .findById(ownerId)
        .orElseThrow(() -> new NoSuchElementException("Owner not found with id: " + ownerId));
  }

  private Property getProperty(Long propertyId) {
    return propertyRepository
        .findById(propertyId)
        .orElseThrow(() -> new NoSuchElementException("Property not found with id: " + propertyId));
  }

  private Booking findBookingById(Long id) {
    return bookingRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + id));
  }

  private Booking findActiveBookingById(Long id) {
    return bookingRepository
        .findActiveBookingByIdAndCanceledIsFalse(id)
        .orElseThrow(() -> new NoSuchElementException("No active booking found with id: " + id));
  }
}
