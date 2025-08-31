package com.example.nice_homeworkTask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Request DTO for POST /suggestTask.
 *
 * Role:
 * - Holds the input fields the API expects.
 * - Jackson maps JSON to this class (requires public no-args ctor + setters).
 * - Bean Validation checks required fields (@NotBlank/@NotNull) before calling
 * the service.
 *
 * Fields (all required):
 * - utterance : the user's free text
 * - userId : who sent the request
 * - sessionId : request/session correlation id
 * - timestamp : when the client sent the request (ISO-8601 Instant)
 *
 * Errors:
 * - Missing/invalid field -> 400 Bad Request (handled by the global exception
 * handler).
 * 
 * Note:
 * Needs a no-args constructor and setters so Jackson can deserialize JSON
 * into this object.
 * Getters are optional for deserialization, but nice to have.
 */

public class NiceHomeworkTaskRequest {

    /** User's text (e.g. "reset password"). Must not be blank. */
    @NotBlank(message = "utterance must not be blank")
    private String utterance;

    /** ID of the user. Must not be blank. */
    @NotBlank(message = "userId must not be blank")
    private String userId;

    /** ID of the session/correlation. Must not be blank. */
    @NotBlank(message = "sessionId must not be blank")
    private String sessionId;

    /** Client timestamp, e.g. "2025-08-21T12:00:00Z". Must not be null. */
    @NotNull(message = "timestamp must not be null")
    private Instant timestamp;

    public NiceHomeworkTaskRequest() {
    }

    // --- Getters & setters ---

    public String getUtterance() {
        return utterance;
    }

    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
