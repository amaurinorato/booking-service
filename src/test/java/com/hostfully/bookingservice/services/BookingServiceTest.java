package com.hostfully.bookingservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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
import com.hostfully.bookingservice.service.BookingService;
import com.hostfully.bookingservice.service.BookingValidationService;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookingServiceTest {

  @Mock private BookingRepository bookingRepository;

  @Mock private PropertyRepository propertyRepository;

  @Mock private GuestRepository guestRepository;

  @Mock private OwnerRepository ownerRepository;

  @Mock private BookingValidationService bookingValidationService;

  private BookingService bookingService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    bookingService =
        new BookingService(
            bookingRepository,
            propertyRepository,
            guestRepository,
            ownerRepository,
            bookingValidationService);
  }

  @Test
  void createBookingShouldThrowExceptionWhenValidationFails() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);

    doThrow(IllegalArgumentException.class)
        .when(bookingValidationService)
        .validateBooking(bookingRequest, null);

    assertThrows(
        IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest));
  }

  @Test
  void updateBookingShouldThrowExceptionWhenBookingNotFound() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);

    when(bookingRepository.findActiveBookingByIdAndCanceledIsFalse(1L))
        .thenReturn(Optional.empty());

    assertThrows(
        NoSuchElementException.class, () -> bookingService.updateBooking(bookingRequest, 1L));
  }

  @Test
  void deleteBookingShouldThrowExceptionWhenBookingNotFound() {
    when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> bookingService.deleteBooking(1L));
  }

  @Test
  void deleteBookingShouldNotThrowExceptionWhenBookingFound() {
    Booking booking = new Booking();
    booking.setId(1L);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    bookingService.deleteBooking(1L);

    verify(bookingRepository, times(1)).delete(booking);
  }

  @Test
  void createBookingShouldReturnBookingResponseWhenValidationPasses() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);
    bookingRequest.setOwnerId(1L);

    Property property = new Property();
    property.setId(1L);

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

    Booking booking = new Booking();
    booking.setStart(bookingRequest.getStart());
    booking.setEnd(bookingRequest.getEnd());
    booking.setProperty(property);

    when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    when(ownerRepository.findById(1L)).thenReturn(Optional.of(new Owner(1L)));

    BookingResponse bookingResponse = bookingService.createBooking(bookingRequest);

    assertEquals(booking.getStart(), bookingResponse.getStart());
    assertEquals(booking.getEnd(), bookingResponse.getEnd());
    assertEquals(booking.getProperty().getId(), bookingResponse.getPropertyId());
    assertNotNull(bookingResponse.getOwnerId());
  }

  @Test
  void updateBookingShouldReturnUpdatedBookingResponseWhenBookingExists() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(3));
    bookingRequest.setEnd(LocalDate.now().plusDays(4));
    bookingRequest.setPropertyId(1L);
    bookingRequest.setGuestId(1L);

    Booking existingBooking = new Booking();
    existingBooking.setId(1L);
    existingBooking.setStart(LocalDate.now().plusDays(1));
    existingBooking.setEnd(LocalDate.now().plusDays(2));
    existingBooking.setProperty(new Property());
    existingBooking.setGuest(new Guest());

    when(bookingRepository.findActiveBookingByIdAndCanceledIsFalse(1L))
        .thenReturn(Optional.of(existingBooking));
    when(guestRepository.findById(1L)).thenReturn(Optional.of(new Guest()));
    when(propertyRepository.findById(1L)).thenReturn(Optional.of(new Property()));

    BookingResponse bookingResponse = bookingService.updateBooking(bookingRequest, 1L);

    assertEquals(bookingRequest.getStart(), bookingResponse.getStart());
    assertEquals(bookingRequest.getEnd(), bookingResponse.getEnd());
  }

  @Test
  void deleteBookingShouldNotThrowExceptionWhenBookingExists() {
    Booking booking = new Booking();
    booking.setId(1L);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    bookingService.deleteBooking(1L);

    verify(bookingRepository, times(1)).delete(booking);
  }

  @Test
  void getBookingShouldReturnBookingResponseWhenBookingExists() {
    Booking booking = new Booking();
    booking.setId(1L);
    booking.setProperty(new Property());

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    BookingResponse bookingResponse = bookingService.getBooking(1L);

    assertNotNull(bookingResponse);
  }

  @Test
  void patchBookingShouldCancelBookingWhenCancelIsTrue() {
    BookingPatchStatus bookingPatchStatus = new BookingPatchStatus();
    bookingPatchStatus.setCancel(true);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setCanceled(false);

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    bookingService.patchBooking(1L, bookingPatchStatus);

    assertTrue(booking.getCanceled());
    verify(bookingRepository, times(1)).save(booking);
  }

  @Test
  void patchBookingShouldValidateAndActivateBookingWhenCancelIsFalse() {
    BookingPatchStatus bookingPatchStatus = new BookingPatchStatus();
    bookingPatchStatus.setCancel(false);

    Booking booking = new Booking();
    booking.setId(1L);
    booking.setCanceled(true);
    booking.setProperty(new Property());
    booking.setStart(LocalDate.now().plusDays(1));
    booking.setEnd(LocalDate.now().plusDays(2));

    when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

    bookingService.patchBooking(1L, bookingPatchStatus);

    assertFalse(booking.getCanceled());
    verify(bookingValidationService, times(1))
        .validateBookingOverlap(
            1L, booking.getProperty().getId(), booking.getStart(), booking.getEnd());
    verify(bookingRepository, times(1)).save(booking);
  }

  @Test
  void createBookingShouldThrowExceptionWhenPropertyOwnerIsDifferentFromRequestOwner() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);
    bookingRequest.setOwnerId(2L);

    Property property = new Property();
    property.setOwner(new Owner(1L));

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
    when(ownerRepository.findById(2L)).thenReturn(Optional.of(new Owner(2L)));
    doCallRealMethod().when(bookingValidationService).validateBlocking(any());

    final var message =
        assertThrows(
                IllegalArgumentException.class, () -> bookingService.createBooking(bookingRequest))
            .getMessage();
    assertEquals("Owner can only block his own property", message);
  }

  @Test
  void updateBookingShouldThrowExceptionWhenPropertyOwnerIsDifferentFromRequestOwner() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);
    bookingRequest.setOwnerId(1L);

    Property property = new Property();
    property.setOwner(new Owner(2L));

    Booking b = new Booking();
    b.setId(1L);
    b.setProperty(property);
    b.setOwner(new Owner(1L));

    when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
    when(ownerRepository.findById(1L)).thenReturn(Optional.of(new Owner(1L)));
    when(bookingRepository.findActiveBookingByIdAndCanceledIsFalse(1L)).thenReturn(Optional.of(b));
    doCallRealMethod().when(bookingValidationService).validateBlocking(any());

    final var message =
        assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateBooking(bookingRequest, 1L))
            .getMessage();
    assertEquals("Owner can only block his own property", message);
  }
}
