package com.hostfully.bookingservice.controller;

import com.hostfully.bookingservice.controller.vo.BookingPatchStatus;
import com.hostfully.bookingservice.controller.vo.BookingRequest;
import com.hostfully.bookingservice.controller.vo.BookingResponse;
import com.hostfully.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

  private final BookingService bookingService;

  @GetMapping("/{id}")
  public ResponseEntity<BookingResponse> getBooking(@PathVariable("id") Long id) {
    log.info("Getting booking for id: {}", id);
    return ResponseEntity.ok(bookingService.getBooking(id));
  }

  @PostMapping
  public ResponseEntity<BookingResponse> createBooking(
      @RequestBody @Valid BookingRequest bookingRequest) {
    log.info("Creating booking for: {}", bookingRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(bookingService.createBooking(bookingRequest));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookingResponse> updateBooking(
      @RequestBody @Valid BookingRequest bookingRequest, @PathVariable("id") Long id) {
    log.info("Updating booking for: {}", bookingRequest);
    return ResponseEntity.status(HttpStatus.OK)
        .body(bookingService.updateBooking(bookingRequest, id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> patchBooking(
      @PathVariable("id") Long id, @RequestBody @Valid BookingPatchStatus bookingPatchStatus) {
    log.info("Patching booking for id: {}. Canceling: {}", id, bookingPatchStatus);
    bookingService.patchBooking(id, bookingPatchStatus);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBooking(@PathVariable("id") Long id) {
    log.info("Deleting booking for id: {}", id);
    bookingService.deleteBooking(id);
    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
