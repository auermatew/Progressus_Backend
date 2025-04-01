package hu.progressus.exceptionhandling;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


@RestControllerAdvice
public class ExceptionsHandler {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<CustomErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
    return new ResponseEntity<>(
        new CustomErrorResponse(exception.getStatusCode().value(), exception.getReason()),
        exception.getStatusCode());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<CustomErrorResponse> handleDtoValidationExceptions(
      ConstraintViolationException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
  }
}
