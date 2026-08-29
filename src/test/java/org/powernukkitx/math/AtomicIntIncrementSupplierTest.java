package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AtomicIntIncrementSupplierTest {

    @Test
    void getAsInt() {
        AtomicIntIncrementSupplier s = new AtomicIntIncrementSupplier(10, 5);
        Assertions.assertEquals(10, s.getAsInt());
        Assertions.assertEquals(15, s.getAsInt());
        Assertions.assertEquals(20, s.getAsInt());
    }

    @Test
    void negativeIncrement() {
        AtomicIntIncrementSupplier s = new AtomicIntIncrementSupplier(0, -2);
        Assertions.assertEquals(0, s.getAsInt());
        Assertions.assertEquals(-2, s.getAsInt());
    }

    @Test
    void stream() {
        AtomicIntIncrementSupplier s = new AtomicIntIncrementSupplier(1, 1);
        int[] values = s.stream().limit(4).toArray();
        Assertions.assertArrayEquals(new int[]{1, 2, 3, 4}, values);
    }
}
