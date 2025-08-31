package com.example.nice_homeworkTask.dto;

import java.time.Instant;

/**
 * Response DTO for POST /suggestTask.
 *
 * Role:
 * - Represents what the server returns to the client.
 * - Jackson turns this object into JSON.
 *
 * Fields:
 * - task: the chosen task ("ResetPasswordTask" / "CheckOrderStatusTask" /
 * "NoTaskFound")
 * - timestamp: server time when the response was created (Instant, ISO-8601)
 *
 * Notes:
 * Needs getters so Jackson can serialize this object to JSON.
 * Keeping a no-args constructor is also useful/conventional.
 * 
 */
public class NiceHomeworkTaskResponse {

    /** The decision result for the given utterance. */
    private String task;

    /** Server-side timestamp for when we built the response. */
    private Instant timestamp;

    public NiceHomeworkTaskResponse() {
    }

    /** Convenience constructor for building the response in the controller. */
    public NiceHomeworkTaskResponse(String task, Instant timestamp) {
        this.task = task;
        this.timestamp = timestamp;
    }

    // --- Getters & setters ---

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
