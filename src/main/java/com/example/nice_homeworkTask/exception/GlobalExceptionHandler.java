package com.example.nice_homeworkTask.exception;

import com.example.nice_homeworkTask.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 *
 * Role:
 * - Turns common exceptions into a clean JSON {@link ErrorResponse} with the
 * right HTTP status.
 * - Keeps controllers clean (no try/catch in each endpoint).
 *
 * Handled here:
 * - MethodArgumentNotValidException -> Bean Validation failed on request DTO ->
 * 400 (with per-field errors).
 * - HttpMessageNotReadableException -> Malformed JSON / wrong field format
 * (e.g., bad Instant) -> 400.
 *
 * Response shape:
 * { "message": "...", "errors": { ... }, "timestamp": "..." }
 *
 * Notes:
 * - @RestControllerAdvice applies to all @RestController classes.
 * - We add server time (Instant.now()) to help with debugging/log correlation.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
     * Function that handle Bean Validation failed (e.g., @NotBlank/@NotNull on the
     * request DTO).
     * collect each field error into a map: field -> message,
     * and return 400 with "Validation failed".
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        // Build a map of field -> validation message
        Map<String, String> errors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            errors.put(err.getField(), err.getDefaultMessage());
        }
        log.warn("Validation failed (userId={}, sessionId={}): {}", MDC.get("userId"), MDC.get("sessionId"), errors); // WARN
                                                                                                                      // log
        ErrorResponse body = new ErrorResponse("Validation failed", errors, Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /*
     * Function that handle JSON body cannot be read or parsed (broken JSON or wrong
     * field format).
     * Example: timestamp is not ISO-8601. Return 400 with a helpful summary.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        String root = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        log.warn("Malformed request body (userId={}, sessionId={}): {}", MDC.get("userId"), MDC.get("sessionId"), root);
        Map<String, String> errors = new HashMap<>();
        errors.put("body", "Invalid JSON or field format");
        errors.put("timestamp", "must be ISO-8601, e.g. 2025-08-21T12:00:00Z");

        ErrorResponse body = new ErrorResponse("Malformed request body", errors, Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
