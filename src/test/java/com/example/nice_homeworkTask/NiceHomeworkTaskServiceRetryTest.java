package com.example.nice_homeworkTask;

import com.example.nice_homeworkTask.service.NiceHomeworkTaskService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/*
 * Unit test for the retry logic using a subclass override the external call.
 *
 * Rationale:
 * - We check how many attempts happen and the final result without reading logs.
 * - Overriding simulateExternalCall makes the test simple and predictable.
 */
class NiceHomeworkTaskServiceRetryTest {

    /*
     * Test double: deterministic external call simulator.
     * Fails on attempts #1 and #2, succeeds on #3.
     * We also count how many times the external call was invoked.
     */
    static class TestableService extends NiceHomeworkTaskService {

        final int failsBeforeSuccess; // how many attempts should fail before success
        int calls; // how many times the "external call" was invoked

        TestableService(int failsBeforeSuccess) {
            this.failsBeforeSuccess = failsBeforeSuccess;
        }

        @Override
        protected void simulateExternalCall(String utterance, int attempt) {
            calls++;
            if (attempt <= failsBeforeSuccess) {
                // simulate transient failures for attempts 1 and 2
                throw new RuntimeException("simulated transient failure (attempt " + attempt + ")");
            }
            // attempt 3: success – do nothing
        }
    }

    // When a task is found: fail on attempts #1 and #2, succeed on attempt #3
    @Test
    void retry_two_failures_then_success_on_third_attempt() {
        var svc = new TestableService(2); // fail, fail, succeed
        String task = svc.suggestTask("check order");
        assertThat(task).isEqualTo("CheckOrderStatusTask");
        assertThat(svc.calls).isEqualTo(3);
    }

    // When no task is found: no calls
    @Test
    void no_retry_when_no_task_found() {
        var svc = new TestableService(0); // config doesn’t matter; no task match ⇒ no calls
        String task = svc.suggestTask("hello there");
        assertThat(task).isEqualTo("NoTaskFound");
        assertThat(svc.calls).isEqualTo(0); // proves no retry was triggered
    }

}
