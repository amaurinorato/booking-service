package com.hostfully.bookingservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class Issue implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int code;

  private String message;

  private List<String> details;

  public Issue(Integer code, String message) {
    this.code = code;
    this.message = message;
  }

  public Issue(final Integer code, final String message, final List<String> details) {
    super();
    this.code = code;
    this.message = message;
    this.details = details;
  }

  public Issue(Integer code, final List<String> details) {
    this.code = code;
    this.details = details;
  }
}
