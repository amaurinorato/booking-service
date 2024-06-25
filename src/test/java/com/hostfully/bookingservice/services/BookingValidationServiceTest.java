package com.hostfully.bookingservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hostfully.bookingservice.controller.vo.BookingRequest;
import com.hostfully.bookingservice.domain.Booking;
import com.hostfully.bookingservice.domain.Owner;
import com.hostfully.bookingservice.domain.Property;
import com.hostfully.bookingservice.repository.BookingRepository;
import com.hostfully.bookingservice.service.BookingValidationService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BookingValidationServiceTest {

  @Mock private BookingRepository bookingRepository;

  private BookingValidationService bookingValidationService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    bookingValidationService = new BookingValidationService(bookingRepository);
  }

  @Test
  void validateBookingShouldThrowExceptionWhenStartDateIsAfterEndDate() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(2));
    bookingRequest.setEnd(LocalDate.now());

    assertThrows(
        IllegalArgumentException.class,
        () -> bookingValidationService.validateBooking(bookingRequest, 1L));
  }

  @Test
  void validateBookingShouldThrowExceptionWhenEndDateIsInPastButCurrentDateIsNot() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(2));
    bookingRequest.setEnd(LocalDate.now().minusDays(3));

    assertThrows(
        IllegalArgumentException.class,
        () -> bookingValidationService.validateBooking(bookingRequest, 1L));
  }

  @Test
  void validateBookingShouldThrowExceptionWhenStartDateIsInPast() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().minusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(1));

    assertThrows(
        IllegalArgumentException.class,
        () -> bookingValidationService.validateBooking(bookingRequest, 1L));
  }

  @Test
  void validateBookingShouldThrowExceptionWhenEndDateIsInPast() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().minusDays(2));
    bookingRequest.setEnd(LocalDate.now().minusDays(1));

    assertThrows(
        IllegalArgumentException.class,
        () -> bookingValidationService.validateBooking(bookingRequest, 1L));
  }

  @Test
  void validateBookingShouldThrowExceptionWhenGuestAndOwnerAreSet() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setGuestId(1L);
    bookingRequest.setOwnerId(1L);

    final var message =
        assertThrows(
                IllegalArgumentException.class,
                () -> bookingValidationService.validateBooking(bookingRequest, 1L))
            .getMessage();
    assertEquals(
        "Guest and owner cannot be set at the same time. Use GuestId to book a property and OwnerId to block a property.",
        message);
  }

  @Test
  void validateBookingShouldNotThrowExceptionWhenOnlyGuestIsSet() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setGuestId(1L);

    bookingValidationService.validateBooking(bookingRequest, 1L);
  }

  @Test
  void validateBookingShouldNotThrowExceptionWhenOnlyOwnerIsSet() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setOwnerId(1L);

    bookingValidationService.validateBooking(bookingRequest, 1L);
  }

  @Test
  void validateBookingShouldThrowExceptionWhenNeitherGuestOrOwnerIsSet() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));

    final var message =
        assertThrows(
                IllegalArgumentException.class,
                () -> bookingValidationService.validateBooking(bookingRequest, 1L))
            .getMessage();
    assertEquals(
        "Guest or owner must be informed. Use GuestId to book a property and OwnerId to block a property.",
        message);
  }

  @Test
  void validateBookingShouldThrowExceptionWhenPropertyIsAlreadyBooked() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);
    bookingRequest.setGuestId(1L);

    final var mockedBooking = new Booking();
    mockedBooking.setId(2L);

    when(bookingRepository.findActiveOverlappingBookings(
            1L, bookingRequest.getStart(), bookingRequest.getEnd()))
        .thenReturn(Collections.singletonList(mockedBooking));

    final var message =
        assertThrows(
                IllegalArgumentException.class,
                () -> bookingValidationService.validateBooking(bookingRequest, 1L))
            .getMessage();
    assertEquals("Property is already booked for the selected dates", message);
  }

  @Test
  void validateBookingShouldThrowExceptionWhenFoundMoreThanOneBooking() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);
    bookingRequest.setOwnerId(1L);

    final var mockedBooking = new Booking();
    mockedBooking.setId(1L);

    final var mockedBooking2 = new Booking();
    mockedBooking.setId(2L);

    when(bookingRepository.findActiveOverlappingBookings(
            1L, bookingRequest.getStart(), bookingRequest.getEnd()))
        .thenReturn(List.of(mockedBooking, mockedBooking2));

    final var message =
        assertThrows(
                IllegalArgumentException.class,
                () -> bookingValidationService.validateBooking(bookingRequest, 1L))
            .getMessage();
    assertEquals("Property is already booked for the selected dates", message);
  }

  @Test
  void validateBookingShouldNotThrowExceptionWhenFoundBookingIsTheSameOfBookingId() {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now().plusDays(1));
    bookingRequest.setEnd(LocalDate.now().plusDays(2));
    bookingRequest.setPropertyId(1L);
    bookingRequest.setGuestId(1L);

    final var mockedBooking = new Booking();
    mockedBooking.setId(1L);

    when(bookingRepository.findActiveOverlappingBookings(
            1L, bookingRequest.getStart(), bookingRequest.getEnd()))
        .thenReturn(Collections.singletonList(mockedBooking));

    bookingValidationService.validateBooking(bookingRequest, 1L);
  }

  @Test
  void validateBlockingShouldNotThrowExceptionWhenOwnerIsBlockingOwnProperty() {
    Booking booking = new Booking();
    Property property = new Property();
    Owner owner = new Owner();
    owner.setId(1L);
    property.setOwner(owner);
    booking.setOwner(owner);
    booking.setProperty(property);

    bookingValidationService.validateBlocking(booking);
  }

  @Test
  void validateBlockingShouldThrowExceptionWhenOwnerIsNotBlockingOwnProperty() {
    Booking booking = new Booking();
    Property property = new Property();
    Owner owner = new Owner();
    Owner anotherOwner = new Owner();
    owner.setId(1L);
    anotherOwner.setId(2L);
    property.setOwner(owner);
    booking.setOwner(anotherOwner);
    booking.setProperty(property);

    final var message =
        assertThrows(
                IllegalArgumentException.class,
                () -> bookingValidationService.validateBlocking(booking))
            .getMessage();
    assertEquals("Owner can only block his own property", message);
  }
}
