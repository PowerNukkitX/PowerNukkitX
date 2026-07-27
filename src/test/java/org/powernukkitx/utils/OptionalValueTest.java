package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

public class OptionalValueTest {
    @Test
    void ofNonNull() {
        OptionalValue<String> v = OptionalValue.of("hi");
        Assertions.assertTrue(v.isPresent());
        Assertions.assertEquals("hi", v.get());
    }

    @Test
    void ofNullThrows() {
        Assertions.assertThrows(NullPointerException.class, () -> OptionalValue.of(null));
    }

    @Test
    void ofNullableAndEmpty() {
        Assertions.assertFalse(OptionalValue.ofNullable(null).isPresent());
        Assertions.assertFalse(OptionalValue.empty().isPresent());
        Assertions.assertTrue(OptionalValue.ofNullable("x").isPresent());
    }

    @Test
    void ifPresent() {
        AtomicReference<String> ref = new AtomicReference<>();
        OptionalValue.of("value").ifPresent(ref::set);
        Assertions.assertEquals("value", ref.get());

        OptionalValue.<String>empty().ifPresent(v -> Assertions.fail());
    }

    @Test
    void orElseAndOrElseGet() {
        Assertions.assertEquals("def", OptionalValue.<String>empty().orElse("def"));
        Assertions.assertEquals("real", OptionalValue.of("real").orElse("def"));
        Assertions.assertEquals("supplied", OptionalValue.<String>empty().orElseGet(() -> "supplied"));
    }

    @Test
    void orElseThrow() {
        Assertions.assertEquals("v", OptionalValue.of("v").orElseThrow(RuntimeException::new));
        Assertions.assertThrows(IllegalStateException.class,
                () -> OptionalValue.empty().orElseThrow(IllegalStateException::new));
    }

    @Test
    void toStringRepresentation() {
        Assertions.assertEquals("OptionalValue[abc]", OptionalValue.of("abc").toString());
        Assertions.assertEquals("OptionalValue.empty", OptionalValue.empty().toString());
    }
}
