package org.powernukkitx.utils.collection.nb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrentAutoIntTableTest {

    @Test
    void freshTableIsZero() {
        var t = new ConcurrentAutoIntTable();
        assertEquals(0L, t.get());
        assertEquals(0, t.intValue());
        assertEquals(0L, t.longValue());
    }

    @Test
    void addAccumulates() {
        var t = new ConcurrentAutoIntTable();
        t.add(5);
        t.add(7);
        assertEquals(12L, t.get());
    }

    @Test
    void incrementAndDecrement() {
        var t = new ConcurrentAutoIntTable();
        t.increment();
        t.increment();
        t.decrement();
        assertEquals(1L, t.get());
    }

    @Test
    void setReplacesValue() {
        var t = new ConcurrentAutoIntTable();
        t.add(100);
        t.set(42);
        assertEquals(42L, t.get());
    }

    @Test
    void estimateMatchesGetForSmallTable() {
        var t = new ConcurrentAutoIntTable();
        t.add(9);
        assertEquals(t.get(), t.estimate_get());
    }

    @Test
    void toStringIsNumericSum() {
        var t = new ConcurrentAutoIntTable();
        t.set(77);
        assertEquals("77", t.toString());
    }

    @Test
    void internalSizeIsPositive() {
        assertTrue(new ConcurrentAutoIntTable().internal_size() > 0);
    }
}
