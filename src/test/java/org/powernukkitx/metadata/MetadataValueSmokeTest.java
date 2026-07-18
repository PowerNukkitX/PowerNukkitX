package org.powernukkitx.metadata;

import org.powernukkitx.plugin.Plugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

/**
 * Pure POJO coverage for the metadata value hierarchy - fixed, soft and lazy values
 * plus the numeric/boolean/string conversion helpers on the adapter. The owning plugin
 * is a bare mock; nothing here needs a live server.
 */
public class MetadataValueSmokeTest {

    private final Plugin plugin = mock(Plugin.class);

    @Test
    void fixedValueHoldsAndConverts() {
        FixedMetadataValue value = new FixedMetadataValue(plugin, 42);
        Assertions.assertEquals(42, value.value());
        Assertions.assertEquals(42, value.asInt());
        Assertions.assertEquals(42L, value.asLong());
        Assertions.assertEquals(42.0f, value.asFloat());
        Assertions.assertEquals(42.0, value.asDouble());
        Assertions.assertEquals((short) 42, value.asShort());
        Assertions.assertEquals((byte) 42, value.asByte());
        Assertions.assertTrue(value.asBoolean());
        Assertions.assertEquals("42", value.asString());
        Assertions.assertSame(plugin, value.getOwningPlugin());

        // invalidate is a no-op for fixed values
        value.invalidate();
        Assertions.assertEquals(42, value.value());
    }

    @Test
    void booleanAndStringConversions() {
        Assertions.assertTrue(new FixedMetadataValue(plugin, Boolean.TRUE).asBoolean());
        Assertions.assertFalse(new FixedMetadataValue(plugin, 0).asBoolean());
        Assertions.assertTrue(new FixedMetadataValue(plugin, "true").asBoolean());
        Assertions.assertFalse(new FixedMetadataValue(plugin, null).asBoolean());
        Assertions.assertEquals("", new FixedMetadataValue(plugin, null).asString());
        Assertions.assertEquals("hi", new FixedMetadataValue(plugin, "hi").asString());
    }

    @Test
    void softFixedValueClearsOnInvalidate() {
        SoftFixedMetaValue value = new SoftFixedMetaValue(plugin, "kept");
        Assertions.assertEquals("kept", value.value());
        value.invalidate();
        Assertions.assertNull(value.value());
    }

    @Test
    void lazyValueEvaluatesAndCaches() {
        int[] calls = {0};
        LazyMetadataValue lazy = new LazyMetadataValue(plugin, () -> {
            calls[0]++;
            return "computed";
        });
        Assertions.assertEquals("computed", lazy.value());
        Assertions.assertEquals("computed", lazy.value());
        // CACHE_AFTER_FIRST_EVAL - only evaluated once
        Assertions.assertEquals(1, calls[0]);

        lazy.invalidate();
        lazy.value();
        Assertions.assertEquals(2, calls[0]);
    }

    @Test
    void lazyValueNeverCacheReEvaluates() {
        int[] calls = {0};
        LazyMetadataValue lazy = new LazyMetadataValue(plugin,
                LazyMetadataValue.CacheStrategy.NEVER_CACHE, () -> {
            calls[0]++;
            return calls[0];
        });
        lazy.value();
        lazy.value();
        Assertions.assertEquals(2, calls[0]);
    }

    @Test
    void lazyValueEternalCacheIgnoresInvalidate() {
        int[] calls = {0};
        LazyMetadataValue lazy = new LazyMetadataValue(plugin,
                LazyMetadataValue.CacheStrategy.CACHE_ETERNALLY, () -> {
            calls[0]++;
            return "eternal";
        });
        Assertions.assertEquals("eternal", lazy.value());
        lazy.invalidate();
        Assertions.assertEquals("eternal", lazy.value());
        Assertions.assertEquals(1, calls[0]);
    }

    @Test
    void lazyValueWrapsFailureInEvaluationException() {
        LazyMetadataValue lazy = new LazyMetadataValue(plugin, () -> {
            throw new IllegalStateException("boom");
        });
        Assertions.assertThrows(MetadataEvaluationException.class, lazy::value);
    }
}
