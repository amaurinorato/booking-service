package com.hostfully.bookingservice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.bookingservice.controller.vo.BookingPatchStatus;
import com.hostfully.bookingservice.controller.vo.BookingRequest;
import com.hostfully.bookingservice.controller.vo.BookingResponse;
import com.hostfully.bookingservice.domain.Booking;
import com.hostfully.bookingservice.domain.Property;
import com.hostfully.bookingservice.service.BookingService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private BookingService bookingService;

  @BeforeEach
  public void setup() {
    Booking booking = new Booking();
    booking.setProperty(new Property());
    BookingResponse bookingResponse = new BookingResponse(booking);
    when(bookingService.getBooking(1L)).thenReturn(bookingResponse);
  }

  @Test
  void getBookingReturnsBookingResponse() throws Exception {
    mockMvc
        .perform(get("/v1/bookings/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void createBookingReturnsCreatedBookingResponse() throws Exception {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setStart(LocalDate.now());
    bookingRequest.setEnd(LocalDate.now().plusDays(1));
    bookingRequest.setPropertyId(1L);
    Booking booking = new Booking();
    booking.setProperty(new Property());
    BookingResponse bookingResponse = new BookingResponse(booking);
    when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(bookingResponse);

    mockMvc
        .perform(
            post("/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
        .andExpect(status().isCreated());
  }

  @Test
  void updateBookingReturnsUpdatedBookingResponse() throws Exception {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setPropertyId(1L);
    bookingRequest.setStart(LocalDate.now());
    bookingRequest.setEnd(LocalDate.now().plusDays(1));
    Booking booking = new Booking();
    booking.setProperty(new Property());
    BookingResponse bookingResponse = new BookingResponse(booking);
    when(bookingService.updateBooking(any(BookingRequest.class), anyLong()))
        .thenReturn(bookingResponse);

    mockMvc
        .perform(
            put("/v1/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void patchBookingReturnsOkStatus() throws Exception {
    BookingPatchStatus bookingPatchStatus = new BookingPatchStatus();
    bookingPatchStatus.setCancel(true);

    mockMvc
        .perform(
            patch("/v1/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingPatchStatus)))
        .andExpect(status().isOk());
  }

  @Test
  void deleteBookingReturnsOkStatus() throws Exception {
    mockMvc
        .perform(delete("/v1/bookings/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void createBookingReturnsBadRequestWhenMandatoryDataIsNotSent() throws Exception {
    BookingRequest bookingRequest = new BookingRequest();

    mockMvc
        .perform(
            post("/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateBookingReturnsBadRequestWhenMandatoryDataIsNotSent() throws Exception {
    BookingRequest bookingRequest = new BookingRequest();

    mockMvc
        .perform(
            put("/v1/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void patchBookingReturnsBadRequestWhenMandatoryDataIsNotSent() throws Exception {
    BookingPatchStatus bookingPatchStatus = new BookingPatchStatus();

    mockMvc
        .perform(
            patch("/v1/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingPatchStatus)))
        .andExpect(status().isBadRequest());
  }
}
