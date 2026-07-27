package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

public class OptionalBooleanTest {
    @Test
    void ofPrimitive() {
        Assertions.assertSame(OptionalBoolean.TRUE, OptionalBoolean.of(true));
        Assertions.assertSame(OptionalBoolean.FALSE, OptionalBoolean.of(false));
    }

    @Test
    void ofNullable() {
        Assertions.assertSame(OptionalBoolean.EMPTY, OptionalBoolean.ofNullable(null));
        Assertions.assertSame(OptionalBoolean.TRUE, OptionalBoolean.ofNullable(Boolean.TRUE));
    }

    @Test
    void presentAndGet() {
        Assertions.assertTrue(OptionalBoolean.TRUE.isPresent());
        Assertions.assertFalse(OptionalBoolean.EMPTY.isPresent());
        Assertions.assertTrue(OptionalBoolean.TRUE.getAsBoolean());
        Assertions.assertFalse(OptionalBoolean.FALSE.getAsBoolean());
    }

    @Test
    void getOnEmptyThrows() {
        Assertions.assertThrows(NoSuchElementException.class, OptionalBoolean.EMPTY::getAsBoolean);
    }

    @Test
    void ifPresent() {
        AtomicBoolean called = new AtomicBoolean(false);
        OptionalBoolean.TRUE.ifPresent(v -> called.set(v));
        Assertions.assertTrue(called.get());

        AtomicBoolean touched = new AtomicBoolean(false);
        OptionalBoolean.EMPTY.ifPresent(v -> touched.set(true));
        Assertions.assertFalse(touched.get());
    }

    @Test
    void orElseAndOrElseGet() {
        Assertions.assertTrue(OptionalBoolean.EMPTY.orElse(true));
        Assertions.assertFalse(OptionalBoolean.FALSE.orElse(true));
        Assertions.assertTrue(OptionalBoolean.EMPTY.orElseGet(() -> true));
        Assertions.assertFalse(OptionalBoolean.FALSE.orElseGet(() -> true));
    }

    @Test
    void orElseThrow() {
        Assertions.assertTrue(OptionalBoolean.TRUE.orElseThrow(RuntimeException::new));
        Assertions.assertThrows(IllegalStateException.class,
                () -> OptionalBoolean.EMPTY.orElseThrow(IllegalStateException::new));
    }

    @Test
    void toStringAndToOptionalValue() {
        Assertions.assertEquals("OptionalBoolean[true]", OptionalBoolean.TRUE.toString());
        Assertions.assertEquals("OptionalBoolean[false]", OptionalBoolean.FALSE.toString());
        Assertions.assertEquals("OptionalBoolean.empty", OptionalBoolean.EMPTY.toString());
        Assertions.assertTrue(OptionalBoolean.TRUE.toOptionalValue().isPresent());
        Assertions.assertFalse(OptionalBoolean.EMPTY.toOptionalValue().isPresent());
    }
}
