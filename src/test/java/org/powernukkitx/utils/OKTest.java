package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OKTest {
    @Test
    void trueConstant() {
        Assertions.assertTrue(OK.TRUE.ok());
        Assertions.assertNull(OK.TRUE.error());
    }

    @Test
    void singleArgConstructor() {
        OK<String> ok = new OK<>(true);
        Assertions.assertTrue(ok.ok());
        Assertions.assertNull(ok.error());
    }

    @Test
    void assertOKPassesWhenOk() {
        Assertions.assertDoesNotThrow(new OK<>(true)::assertOK);
    }

    @Test
    void assertOKThrowsWithStringError() {
        OK<String> fail = new OK<>(false, "boom");
        AssertionError e = Assertions.assertThrows(AssertionError.class, fail::assertOK);
        Assertions.assertEquals("boom", e.getMessage());
    }

    @Test
    void assertOKThrowsWithThrowableError() {
        Exception cause = new IllegalStateException("bad");
        OK<Exception> fail = new OK<>(false, cause);
        AssertionError e = Assertions.assertThrows(AssertionError.class, fail::assertOK);
        Assertions.assertSame(cause, e.getCause());
    }

    @Test
    void getErrorWrapsThrowable() {
        Exception cause = new RuntimeException("x");
        OK<Exception> fail = new OK<>(false, cause);
        Assertions.assertSame(cause, fail.getError().getCause());
    }

    @Test
    void getErrorWithNullUsesUnknown() {
        OK<Object> fail = new OK<>(false, null);
        Assertions.assertEquals("Unknown error", fail.getError().getMessage());
    }
}
