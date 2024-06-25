package com.hostfully.bookingservice.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = INTERNAL_SERVER_ERROR)
  protected Issue processExceptions(final Exception ex, final WebRequest request) {

    return new Issue(500, Collections.singletonList(ex.getLocalizedMessage()));
  }

  @ExceptionHandler({
    ValidationException.class,
    NoSuchElementException.class,
    IllegalArgumentException.class
  })
  @ResponseStatus(value = BAD_REQUEST)
  protected Issue exceptions(final Exception ex) {

    return new Issue(400, Collections.singletonList(ex.getLocalizedMessage()));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(value = BAD_REQUEST)
  protected Issue handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex, final HttpServletRequest request) {

    final List<String> errors = new ArrayList<>();
    for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }

    return new Issue(400, errors);
  }
}
