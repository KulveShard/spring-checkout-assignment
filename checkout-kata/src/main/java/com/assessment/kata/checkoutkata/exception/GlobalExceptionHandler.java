package com.assessment.kata.checkoutkata.exception;

import com.assessment.kata.checkoutkata.dto.error.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.put(error.getField(), error.getDefaultMessage());
    });

    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(ItemNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleItemNotFound(ItemNotFoundException ex) {
    log.error("Item not found: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    errors.put("message", "Item not found");
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(PricingValidationException.class)
  public ResponseEntity<Map<String, String>> handlePricingValidation(PricingValidationException ex) {
    log.error("Pricing validation error: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    errors.put("message", ex.getMessage());
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleInvalidArgument(IllegalArgumentException ex) {
    log.error("Invalid argument: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    errors.put("message", "Invalid item");
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDTO> handleValidationError(ConstraintViolationException ex) {
    ErrorResponseDTO error = new ErrorResponseDTO(
        "VALIDATION_ERROR",
        ex.getMessage(),
        LocalDateTime.now()
    );
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneralError(Exception ex) {
    log.error("Unexpected error occurred", ex);
    ErrorResponseDTO error = new ErrorResponseDTO(
        "INTERNAL_ERROR",
        "An unexpected error occurred",
        LocalDateTime.now()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
