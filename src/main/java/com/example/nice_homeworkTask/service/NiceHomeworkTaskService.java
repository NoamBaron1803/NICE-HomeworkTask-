package com.example.nice_homeworkTask.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*
 * This service maps a free-text utterance to a task name.
 * Bonus: it simulates an external dependency that may fail and retries up to 3 times.
 * The retry is logging-only and never changes the HTTP response.
 */
@Service
public class NiceHomeworkTaskService {

    // If we can't find a match, return NoTaskFound.
    private static final String NO_TASK = "NoTaskFound";

    // Logger for printing messages to the console (info/warn/error).
    private static final Logger log = LoggerFactory.getLogger(NiceHomeworkTaskService.class);

    // Max distance (in characters) allowed between the two keywords forward
    // patterns.
    // e.g., "reset ... password", "password ... reset").
    private static final int GAP = 15;

    private static final String ORDER_EXCEPT_IN_ORDER_TO = "(?:(?<!\\bin\\s)\\border\\b|\\border\\b(?!\\s+to))";

    /**
     * Patterns for "ResetPasswordTask".
     * Includes:
     * - forward patterns with GAP : "reset ... password", "forgot ... password"
     * - reverse contiguous only: "password reset" (We keep the reverse pattern
     * contiguous to avoid noisy matches)
     * - exact two-word phrases: "reset password", "forgot password"
     */

    private static final List<Pattern> RESET_PASSWORD_PATTERNS = List.of(
            // "reset ... password"
            Pattern.compile("\\breset\\b[\\s\\S]{0," + GAP + "}\\bpassword\\b", Pattern.CASE_INSENSITIVE),

            // "forgot ... password" (e.g., "I forgot my password")
            Pattern.compile("\\bforgot\\b[\\s\\S]{0," + GAP + "}\\bpassword\\b", Pattern.CASE_INSENSITIVE),

            // reverse, contiguous only: "password reset", "forgot password"
            Pattern.compile("\\bpassword\\s+reset\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bpassword\\s+forgot\\b", Pattern.CASE_INSENSITIVE),

            // exact two-word phrases
            Pattern.compile("\\breset password\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bforgot password\\b", Pattern.CASE_INSENSITIVE));

    /**
     * Patterns for "CheckOrderStatusTask".
     * Includes:
     * - forward patterns with GAP: "check ... order", "track ... order"
     * - reverse contiguous only: "order check", "order track" (We keep the reverse
     * pattern contiguous to avoid noisy matches)
     * - exact two-word phrases: "check order", "track order"
     */
    private static final List<Pattern> CHECK_ORDER_PATTERNS = List.of(
            // "check ... order"
            Pattern.compile("\\bcheck\\b[\\s\\S]{0," + GAP + "}" + ORDER_EXCEPT_IN_ORDER_TO, Pattern.CASE_INSENSITIVE),

            // "track ... order"
            Pattern.compile("\\btrack\\b[\\s\\S]{0," + GAP + "}" + ORDER_EXCEPT_IN_ORDER_TO, Pattern.CASE_INSENSITIVE),

            // reverse, contiguous only
            Pattern.compile("\\border\\s+check\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\border\\s+track\\b", Pattern.CASE_INSENSITIVE),

            // exact two-word phrases
            Pattern.compile("\\bcheck order\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\btrack order\\b", Pattern.CASE_INSENSITIVE));

    // Map from task name -> the list of patterns for that task.
    // Add more tasks/patterns here in the future if needed.
    private static final Map<String, List<Pattern>> TASK_PATTERNS = Map.of(
            "ResetPasswordTask", RESET_PASSWORD_PATTERNS,
            "CheckOrderStatusTask", CHECK_ORDER_PATTERNS);

    /**
     * Main function used by the controller.
     * 1) If the text is empty -> return "NoTaskFound".
     * 2) Find which task appears first in the text (by regex position).
     * 3) If we found a task, call a fake external service with retry (bonus).
     * 4) Return the task name.
     */
    public String suggestTask(String utterance) {
        // Basic check: null or only spaces -> return "NoTaskFound"
        if (utterance == null || utterance.trim().isEmpty()) {
            return NO_TASK;
        }

        // Find the fist matching task
        String task = findFirstMatchTask(utterance);

        // 3) Bonus: simulate an external call with retry (does not change the result).
        if (!NO_TASK.equals(task)) {
            callExternalWithRetry(utterance);
        }

        return task;
    }

    /**
     * Returns the task whose first regex match appears earliest in the text.
     * If nothing matches, returns "NoTaskFound"..
     */
    private String findFirstMatchTask(String utterance) {

        String text = utterance.replaceAll("\\s+", " ");
        int bestPos = Integer.MAX_VALUE;
        String bestTask = NO_TASK;

        for (Map.Entry<String, List<Pattern>> entry : TASK_PATTERNS.entrySet()) {
            String taskName = entry.getKey();
            for (Pattern p : entry.getValue()) {
                Matcher m = p.matcher(text);
                if (m.find()) {
                    int pos = m.start();
                    if (pos < bestPos) {
                        bestPos = pos;
                        bestTask = taskName;
                    }
                }
            }
        }
        return bestTask;
    }

    /**
     * Bonus: simple retry demo
     * Try a fake external call up to 3 times.
     * - Attempt 1: fail (we throw an exception inside simulateExternalCall)
     * - Attempt 2: fail
     * - Attempt 3: success
     *
     * We only log; we never rethrow. The HTTP response remains unchanged.
     */
    private void callExternalWithRetry(String utterance) {
        final int maxAttempts = 3;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                simulateExternalCall(utterance, attempt);
                log.info("External call succeeded on attempt {}", attempt);
                return; // success -> stop retrying
            } catch (RuntimeException ex) {
                if (attempt < maxAttempts) {
                    log.warn("External call failed on attempt {}/{}: {}", attempt, maxAttempts, ex.getMessage());
                } else {
                    log.error("External call failed after {} attempts; continuing without it.", maxAttempts);
                }
            }
        }
    }

    /**
     * Fake external call (for the bonus).
     * Fails on attempts #1 and #2, succeeds on #3.
     * Deterministic by design to keep tests stable.
     */
    protected void simulateExternalCall(String utterance, int attempt) {
        if (attempt < 3) {
            throw new RuntimeException("Simulated upstream failure (attempt " + attempt + ")");
        }
        // Attempt #3: success -> do nothing.
    }

    /* private static final Map<String, String> DICTIONARY; */

    /*
     * static {
     * LinkedHashMap<String, String> m = new LinkedHashMap<>();
     * m.put("reset password", "ResetPasswordTask");
     * m.put("forgot password", "ResetPasswordTask");
     * m.put("check order", "CheckOrderStatusTask");
     * m.put("track order", "CheckOrderStatusTask");
     * DICTIONARY = Collections.unmodifiableMap(m);
     * }
     */

    /*
     * public String suggestTask(String utterance) {
     * if (utterance == null)
     * return NO_TASK;
     * String lower = utterance.toLowerCase();
     * for (Map.Entry<String, String> e : DICTIONARY.entrySet()) {
     * if (lower.contains(e.getKey()))
     * return e.getValue();
     * }
     * return NO_TASK;
     * }
     */

}
