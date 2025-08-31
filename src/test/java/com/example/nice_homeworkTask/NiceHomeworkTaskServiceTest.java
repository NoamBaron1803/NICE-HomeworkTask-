package com.example.nice_homeworkTask;

import com.example.nice_homeworkTask.service.NiceHomeworkTaskService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests
 *
 * What this file verifies:
 * 1) Matching logic only (no HTTP/server layer).
 * 2) Typical user phrases and case-insensitivity.
 * 3) Punctuation/hyphens/newlines don’t break matches.
 * 4) Reverse order (e.g., "password reset", "order check").
 * 5) GAP rule: keywords can be up to 15 characters apart.
 * 6) If both intents appear, the first match in the text wins.
 * 7) Negative cases that must NOT match.
 */

public class NiceHomeworkTaskServiceTest {

    // ===== Basic OK cases =====
    @Test
    void resetPassword_Test1() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("please RESET password"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test2() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("ohh, i forgot PaSSword, can you help me"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test3() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("how to change my username and also please reset password"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test4() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("please, reset password!!"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test5() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("I forgot MY PASSWORD"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test6() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("password reset please"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test7() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("please i need to reset very fast my password"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test8() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("please i forgot my importent password"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test9() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("password forgot"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void resetPassword_Test10() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("please reset-password now"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void checkOrder_Test1() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("CAN I CHECK ORDER 123?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test2() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("can I track order 123?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test3() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("can I track my importent order 123?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test4() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("can I check a very urgent order 123?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test5() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("order check please to number 123?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test6() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("can you help with order track?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test7() {
        var svc = new NiceHomeworkTaskService();
        // Newline between keywords - inside GAP
        assertThat(svc.suggestTask("can you help with order \n track?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test8() {
        var svc = new NiceHomeworkTaskService();
        String gap15 = "aaaaaaaaaaaaa"; // 13 chars
        String utterance = "track " + gap15 + " order";
        assertThat(svc.suggestTask(utterance)).isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test9() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("can you check-order 42?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test10() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("check order to track later"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test11() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("can you check-order 42?"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void checkOrder_Test12() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("in order check please!"))
                .isEqualTo("CheckOrderStatusTask");
    }

    // ===== Earliest match wins cases =====
    @Test
    void earliestWins_resetPassword_Test1() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("please password reset and then track order"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void earliestWins_resetPassword_Test2() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("please reset my password and then track order"))
                .isEqualTo("ResetPasswordTask");
    }

    @Test
    void earliestWins_checkOrder_Test1() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("let’s track order first, then please reset password"))
                .isEqualTo("CheckOrderStatusTask");
    }

    @Test
    void earliestWins_checkOrder_Test2() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("let’s track my order first, then please reset password"))
                .isEqualTo("CheckOrderStatusTask");
    }

    // ===== NoTaskFound basic cases =====
    @Test
    void NoTaskFound_Test1() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("how to change my email"))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test2() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("")).isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test3() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask(null)).isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test4() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("   \t   ")).isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test5() {
        var svc = new NiceHomeworkTaskService();
        // 20 chars between the words -> exceeds GAP = 15
        String utterance = "reset 12345678901234567890 password";
        assertThat(svc.suggestTask(utterance))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test7() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("ORDERcheck")).isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test8() {
        var svc = new NiceHomeworkTaskService();
        String gap16 = "aaaaaaaaaaaaaaaa"; // 16 chars
        String utterance = "forgot " + gap16 + " password";
        assertThat(svc.suggestTask(utterance)).isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test9() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("my passwords reset please"))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test10() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("can you help with orders track?"))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test11() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("preorder check"))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test12() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("in order to check something"))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test13() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("password !! reset please"))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test14() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("in order to track!!"))
                .isEqualTo("NoTaskFound");
    }

    @Test
    void NoTaskFound_Test15() {
        var svc = new NiceHomeworkTaskService();
        assertThat(svc.suggestTask("track   in   order   to   be sure"))
                .isEqualTo("NoTaskFound");
    }
}
