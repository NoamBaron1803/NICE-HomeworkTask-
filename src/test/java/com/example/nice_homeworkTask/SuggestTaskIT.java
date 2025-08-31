package com.example.nice_homeworkTask;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for /suggestTask.
 *
 * What we test here:
 * - Full HTTP flow (controller + validation + JSON binding + service) on a
 * random port.
 * - 200 OK responses for valid inputs and correct "task" value.
 * - 400/405 error responses for invalid inputs or wrong HTTP method.
 * - Matching edge cases (reverse order, newline between keywords, GAP
 * boundary).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SuggestTaskIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    // -------- Helpers --------

    /* Builds the tested URL with the random port. */
    private String url() {
        return "http://localhost:" + port + "/suggestTask";
    }

    /* Returns JSON headers (Content-Type: application/json). */
    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    /**
     * Builds a minimal valid request body with all required fields.
     * You can remove/change fields in the test after calling this.
     */
    private Map<String, Object> baseBody(String utterance) {
        Map<String, Object> body = new HashMap<>();
        body.put("utterance", utterance);
        body.put("userId", "12345");
        body.put("sessionId", "abcde-67890");
        body.put("timestamp", Instant.now().toString()); // ISO-8601
        return body;
    }

    // ===== OK cases =====

    @Test
    void resetPassword_Test1_ok() {
        Map<String, Object> body = baseBody("please reset password");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        // Assert: 200 and correct task
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("ResetPasswordTask");
        assertThat(res.getBody().get("timestamp")).isNotNull();
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void resetPassword_Test2_ok() {
        Map<String, Object> body = baseBody("I FORGOT password please");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("ResetPasswordTask");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void checkOrder_Test1_ok() {
        Map<String, Object> body = baseBody("can I track order 123?");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("CheckOrderStatusTask");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void checkOrder_Test2_ok() {
        Map<String, Object> body = baseBody("check my order please"); // "check ... order"
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("CheckOrderStatusTask");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void earliestWins_trackOrderFirst_ok() {
        // Both intents appear; "track order" appears earlier => CheckOrderStatusTask
        Map<String, Object> body = baseBody("let's track order first, but please reset password too");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("CheckOrderStatusTask");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void resetPassword_reverseContiguous_ok() {
        // Reverse, contiguous pattern "password reset"
        Map<String, Object> body = baseBody("password reset please");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("ResetPasswordTask");
    }

    @Test
    void checkOrder_reverseContiguous_orderCheck_ok() {
        // Reverse, contiguous pattern "order check"
        Map<String, Object> body = baseBody("order check please");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_reverseContiguous_orderTrack_ok() {
        // Reverse, contiguous pattern "order track"
        Map<String, Object> body = baseBody("order track now please");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void resetPassword_newlineBetweenKeywords_ok() {
        // Newline between the two keywords -> should still match (due to GAP rule)
        Map<String, Object> body = baseBody("please forgot \n password now");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_hyphenated_ok() {
        // Hyphenated form "forgot-password" should still match our pattern family
        Map<String, Object> body = baseBody("please forgot-password now");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("ResetPasswordTask");
    }

    @Test
    void checkOrder_gapBoundaryExactly15_ok() {
        String gap15 = "aaaaaaaaaaaaa"; // 13 chars (GAP=15)
        // Boundary test: exactly 15 chars between keywords -> should match
        Map<String, Object> body = baseBody("track " + gap15 + " order");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void ExtraFieldIgnored_Test_ok() {
        // Extra field in the JSON should be ignored by Jackson -> still 200 OK
        Map<String, Object> body = baseBody("reset password");
        body.put("extraField", "should be ignored");

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("ResetPasswordTask");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void noMatch_returnsNoTaskFound_Test_ok() {
        // Valid request, but no keyword match -> 200 OK with "NoTaskFound"
        Map<String, Object> body = baseBody("how to change my email?");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("NoTaskFound");
        assertThat(res.getBody().get("timestamp")).isNotNull();
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void earliestWins_resetPasswordFirst_ok() {
        // Both intents appear; "reset password" appears earlier => ResetPasswordTask
        Map<String, Object> body = baseBody("please reset password, then track order");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("ResetPasswordTask");
    }

    @Test
    void gapTooLarge_NoTaskFound_ok() {
        String gap16 = "aaaaaaaaaaaaaaaa"; // 16 chars (> GAP)
        // 16 chars between the two words (GAP=15) -> should NOT match
        Map<String, Object> body = baseBody("forgot " + gap16 + " password");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("task")).isEqualTo("NoTaskFound");
    }

    // ===== 400/405 error cases =====

    @Test
    void missingUtterance_Test_400_validationFailed() {
        // Missing "utterance"
        Map<String, Object> body = baseBody("reset password");
        body.remove("utterance");

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void blankUtterance_Test_400_validationFailed() {
        // Blank "utterance" (spaces)
        Map<String, Object> body = baseBody("   ");
        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void malformedTimestamp_Test_400_malformedBody() {
        // Wrong timestamp format -> JSON binding fails -> handled by
        // GlobalExceptionHandler
        String badJson = """
                {
                  "utterance": "reset password",
                  "userId": "12345",
                  "sessionId": "abcde-67890",
                  "timestamp": "21-08-2025 12:00"
                }
                """;

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(badJson, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Malformed request body");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void missingUserId_Test_400_validationFailed() {
        // Missing userId
        Map<String, Object> body = baseBody("reset password");
        body.remove("userId");

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void multipleFieldsMissing_Test_400_validationFailed() {
        // Only utterance provided; userId/sessionId/timestamp are missing -> 400
        Map<String, Object> body = new HashMap<>();
        body.put("utterance", "reset password");

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void missingTimestamp_Test_400_validationFailed() {
        // Missing timestamp
        Map<String, Object> body = baseBody("reset password");
        body.remove("timestamp");

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void missingSessionId_Test_400_validationFailed() {
        // Missing sessionId
        Map<String, Object> body = baseBody("reset password");
        body.remove("sessionId");

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Validation failed");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void jsonMalformedSyntax_Test_400_malformedBody() {
        // Broken JSON (missing closing brace) -> 400 Malformed request body
        String brokenJson = "{\"utterance\":\"reset password\",\"userId\":\"12345\"";

        ResponseEntity<Map> res = rest.postForEntity(url(), new HttpEntity<>(brokenJson, jsonHeaders()), Map.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Malformed request body");
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void wrongMethodGet_Test_405_methodNotAllowed() {
        // suggestTask only supports POST. GET should return 405 Method Not Allowed
        ResponseEntity<String> res = rest.getForEntity(url(), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void emptyBody_Test_400_validationFailed() {
        // Empty JSON object -> all required fields missing -> 400 Validation failed
        ResponseEntity<Map> res = rest.postForEntity(
                url(),
                new HttpEntity<>("{}", jsonHeaders()),
                Map.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().get("message")).isEqualTo("Validation failed");
    }
}
