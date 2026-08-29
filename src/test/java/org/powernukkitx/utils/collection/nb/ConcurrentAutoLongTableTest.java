package org.powernukkitx.utils.collection.nb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrentAutoLongTableTest {

    @Test
    void freshTableIsZero() {
        var t = new ConcurrentAutoLongTable();
        assertEquals(0L, t.get());
        assertEquals(0, t.intValue());
        assertEquals(0L, t.longValue());
    }

    @Test
    void addAccumulatesLargeValues() {
        var t = new ConcurrentAutoLongTable();
        t.add(5_000_000_000L);
        t.add(1L);
        assertEquals(5_000_000_001L, t.get());
    }

    @Test
    void incrementAndDecrement() {
        var t = new ConcurrentAutoLongTable();
        t.increment();
        t.increment();
        t.decrement();
        assertEquals(1L, t.get());
    }

    @Test
    void setReplacesValue() {
        var t = new ConcurrentAutoLongTable();
        t.add(100L);
        t.set(42L);
        assertEquals(42L, t.get());
    }

    @Test
    void estimateMatchesGetForSmallTable() {
        var t = new ConcurrentAutoLongTable();
        t.add(9L);
        assertEquals(t.get(), t.estimate_get());
    }

    @Test
    void toStringIsNumericSum() {
        var t = new ConcurrentAutoLongTable();
        t.set(77L);
        assertEquals("77", t.toString());
    }

    @Test
    void internalSizeIsPositive() {
        assertTrue(new ConcurrentAutoLongTable().internal_size() > 0);
    }
}
