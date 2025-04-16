package hu.progressus.exceptionhandling;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;


@RestControllerAdvice
public class ExceptionsHandler {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<CustomErrorResponse> handleResponseStatusException(ResponseStatusException exception) {
    return new ResponseEntity<>(
        new CustomErrorResponse(exception.getStatusCode().value(), exception.getReason()),
        exception.getStatusCode());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    String errorMessage = exception.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.joining("; "));

    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<CustomErrorResponse> handleDtoValidationExceptions(
      ConstraintViolationException exception) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.getStatus()));
  }
  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<CustomErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
    CustomErrorResponse errorResponse = new CustomErrorResponse(
        HttpStatus.UNAUTHORIZED.value(),
        "JWT token has expired. Please log in again."
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }
}
