package com.example.nice_homeworkTask.controller;

import com.example.nice_homeworkTask.dto.NiceHomeworkTaskRequest;
import com.example.nice_homeworkTask.dto.NiceHomeworkTaskResponse;
import com.example.nice_homeworkTask.service.NiceHomeworkTaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * REST controller for /suggestTask — the HTTP boundary of the app.
 *
 * Rule:
 * - Exposes POST /suggestTask that accepts a JSON request
 * (NiceHomeworkTaskRequest).
 * - Validates fields via Bean Validation (@Valid on the DTO).
 * - Delegates the “which task fits this utterance?” decision to
 * NiceHomeworkTaskService.
 * - Wraps the result into NiceHomeworkTaskResponse and returns 200 OK as JSON.
 * - Logs a small “in/out” trail for observability.
 *
 * What it does NOT do:
 * - No regex or business logic here (lives in the service for testability &
 * separation of concerns).
 * - No persistence or external calls here.
 *
 * Contract:
 * Input JSON -> { utterance, userId, sessionId, timestamp } // all required
 * Output JSON -> { task, timestamp } // task ∈ { ResetPasswordTask,
 * CheckOrderStatusTask, NoTaskFound }
 *
 * Errors:
 * - Invalid/missing fields -> 400 Bad Request (handled by @Valid + global
 * exception handler).
 * - Valid input but no match -> still 200 OK with task="NoTaskFound".
 */

@RestController
public class NiceHomeworkTaskController {

    // Logger to print basic request/response info
    private static final Logger log = LoggerFactory.getLogger(NiceHomeworkTaskController.class);
    private final NiceHomeworkTaskService service;

    public NiceHomeworkTaskController(NiceHomeworkTaskService service) {
        this.service = service;
    }

    /**
     * POST /suggestTask
     * Accepts a JSON body (validated), computes the task, and returns 200 with the
     * result.
     * On validation errors, a 400 is returned by the global exception handler.
     */
    @PostMapping("/suggestTask")
    public ResponseEntity<NiceHomeworkTaskResponse> suggestTask(@Valid @RequestBody NiceHomeworkTaskRequest req) {

        log.info("Received suggestTask: userId={}, sessionId={}, timestamp={}, utterance='{}'",
                req.getUserId(), req.getSessionId(), req.getTimestamp(), req.getUtterance());

        // Delegate to service to decide which task fits the utterance
        String task = service.suggestTask(req.getUtterance());

        // Build response with the chosen task + current server time
        NiceHomeworkTaskResponse res = new NiceHomeworkTaskResponse(task, Instant.now());

        log.info("Responding task='{}' for userId={} sessionId={}", task, req.getUserId(), req.getSessionId());
        return ResponseEntity.ok(res);
    }
}
