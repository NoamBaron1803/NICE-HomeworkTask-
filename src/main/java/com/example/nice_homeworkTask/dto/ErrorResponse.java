package com.example.nice_homeworkTask.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Error response DTO returned by the global exception handler.
 *
 * Role:
 * - Wraps error details sent back to the client when a request fails.
 * - Built by the GlobalExceptionHandler (validation errors, malformed JSON,
 * etc.).
 *
 * Fields:
 * - message: short summary (e.g., "Validation failed", "Malformed request
 * body").
 * - errors: optional map of field to validation message (only for 400
 * validation cases).
 * - timestamp: server time when the error was created (Instant).
 *
 * Note:
 * - Needs a public no-args constructor and getters so Jackson can serialize to
 * JSON.
 * - Setters are kept for completeness / potential deserialization.
 *
 * Example JSON:
 * {
 * "message": "Validation failed",
 * "errors": { "userId": "must not be blank", "timestamp": "must not be null" },
 * "timestamp": "2025-08-31T10:15:30Z"
 * }
 */
public class ErrorResponse {

    /* Short summary of the error. */
    private String message;

    /**
     * Field-specific validation errors (may be null for non-validation errors).
     * Key = field name, Value = error message.
     */
    private Map<String, String> errors;

    /** Server-side time of the error (ISO-8601). */
    private Instant timestamp;

    public ErrorResponse() {
    }

    // Constructor
    public ErrorResponse(String message, Map<String, String> errors, Instant timestamp) {
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    // --- Getters & setters ---

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
