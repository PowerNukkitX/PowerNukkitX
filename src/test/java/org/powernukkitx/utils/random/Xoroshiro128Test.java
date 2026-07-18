package org.powernukkitx.utils.random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Xoroshiro128Test {
    @Test
    void sameSeedSameSequence() {
        Xoroshiro128 a = new Xoroshiro128(12345L);
        Xoroshiro128 b = new Xoroshiro128(12345L);
        for (int i = 0; i < 100; i++) {
            Assertions.assertEquals(a.nextLong(), b.nextLong());
        }
    }

    @Test
    void identicalReproducesSequence() {
        Xoroshiro128 a = new Xoroshiro128(999L);
        Xoroshiro128 copy = a.identical();
        Assertions.assertEquals(999L, copy.getSeed());
        for (int i = 0; i < 50; i++) {
            Assertions.assertEquals(a.nextLong(), copy.nextLong());
        }
    }

    @Test
    void setSeedResets() {
        Xoroshiro128 r = new Xoroshiro128(1L);
        long first = r.nextLong();
        r.setSeed(1L);
        Assertions.assertEquals(first, r.nextLong());
        Assertions.assertEquals(1L, r.getSeed());
    }

    @Test
    void nextIntNonNegative() {
        Xoroshiro128 r = new Xoroshiro128(42L);
        for (int i = 0; i < 1000; i++) {
            Assertions.assertTrue(r.nextInt() >= 0);
        }
    }

    @Test
    void nextIntWithZeroMax() {
        Xoroshiro128 r = new Xoroshiro128(7L);
        Assertions.assertEquals(0, r.nextInt(0));
    }

    @Test
    void nextIntBoundedInRange() {
        Xoroshiro128 r = new Xoroshiro128(7L);
        for (int i = 0; i < 1000; i++) {
            int v = r.nextInt(10);
            Assertions.assertTrue(v >= 0 && v < 10, "value " + v);
        }
    }

    @Test
    void nextDoubleAndFloatInUnitInterval() {
        Xoroshiro128 r = new Xoroshiro128(3L);
        for (int i = 0; i < 1000; i++) {
            double d = r.nextDouble();
            Assertions.assertTrue(d >= 0.0 && d < 1.0);
            float f = r.nextFloat();
            Assertions.assertTrue(f >= 0.0f && f < 1.0f);
        }
    }

    @Test
    void forkIsDeterministicFromSeed() {
        Xoroshiro128 a = new Xoroshiro128(55L);
        Xoroshiro128 b = new Xoroshiro128(55L);
        Assertions.assertEquals(a.fork().getSeed(), b.fork().getSeed());
    }

    @Test
    void nextBoundedInt() {
        Xoroshiro128 r = new Xoroshiro128(8L);
        for (int i = 0; i < 1000; i++) {
            int v = r.nextBoundedInt(5);
            Assertions.assertTrue(v >= 0 && v <= 5, "value " + v);
        }
    }
}
