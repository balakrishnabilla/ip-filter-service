package com.ipfilterservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalTime;
import java.util.Optional;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
  private Optional<ObjectError> firstError;

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ExceptionResponse> handleAllExceptions(
      Exception ex, WebRequest request) {
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(LocalTime.now(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    final Optional<ObjectError> firstError =
        ex.getBindingResult().getAllErrors().stream().findFirst();
    String firstErrorMessage = null;
    if (firstError.isPresent()) {
      firstErrorMessage = firstError.get().toString();
    }
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(LocalTime.now(), firstErrorMessage, request.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }
}
