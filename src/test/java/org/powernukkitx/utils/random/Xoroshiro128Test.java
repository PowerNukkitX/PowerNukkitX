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

    /** nextInt() returns the lower 32 bits of nextLong(), including negative values. */
    @Test
    void nextIntIsLower32BitsOfNextLong() {
        Xoroshiro128 ints = new Xoroshiro128(42L);
        Xoroshiro128 longs = new Xoroshiro128(42L);
        for (int i = 0; i < 1000; i++) {
            Assertions.assertEquals((int) longs.nextLong(), ints.nextInt());
        }
    }

    @Test
    void nextIntWithNonPositiveMaxReturnsZero() {
        Xoroshiro128 r = new Xoroshiro128(7L);
        Assertions.assertEquals(0, r.nextInt(0));
        Assertions.assertEquals(0, r.nextInt(-3));
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
    void nextExclusiveIntRejectsNonPositiveBound() {
        Xoroshiro128 r = new Xoroshiro128(7L);
        Assertions.assertThrows(IllegalArgumentException.class, () -> r.nextExclusiveInt(0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> r.nextExclusiveInt(-5));
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

    /** nextBoundedInt has an inclusive upper bound. */
    @Test
    void nextBoundedInt() {
        Xoroshiro128 r = new Xoroshiro128(8L);
        for (int i = 0; i < 1000; i++) {
            int v = r.nextBoundedInt(5);
            Assertions.assertTrue(v >= 0 && v <= 5, "value " + v);
        }
    }

    /** nextInt(min, max) has inclusive bounds. */
    @Test
    void nextIntBetweenIsInclusive() {
        Xoroshiro128 r = new Xoroshiro128(11L);
        boolean sawMin = false;
        boolean sawMax = false;
        for (int i = 0; i < 2000; i++) {
            int v = r.nextInt(3, 6);
            Assertions.assertTrue(v >= 3 && v <= 6, "value " + v);
            sawMin |= v == 3;
            sawMax |= v == 6;
        }
        Assertions.assertTrue(sawMin && sawMax);
    }

    @Test
    void ofSeedMatchesConstructor() {
        Xoroshiro128 a = Xoroshiro128.ofSeed(2024L);
        Xoroshiro128 b = new Xoroshiro128(2024L);
        for (int i = 0; i < 50; i++) {
            Assertions.assertEquals(a.nextLong(), b.nextLong());
        }
    }

    @Test
    void positionalFactoryIsDeterministicAndOrderIndependent() {
        Xoroshiro128PositionalFactory f1 = new Xoroshiro128(123L).forkPositional();
        Xoroshiro128PositionalFactory f2 = new Xoroshiro128(123L).forkPositional();
        Assertions.assertEquals(f1.getLo(), f2.getLo());
        Assertions.assertEquals(f1.getHi(), f2.getHi());

        long a1 = f1.hashOf("minecraft:a").nextLong();
        long b1 = f1.hashOf("minecraft:b").nextLong();
        long b2 = f2.hashOf("minecraft:b").nextLong();
        long a2 = f2.hashOf("minecraft:a").nextLong();
        Assertions.assertEquals(a1, a2);
        Assertions.assertEquals(b1, b2);
        Assertions.assertNotEquals(a1, b1);
    }

    @Test
    void positionalAtIsDeterministicPerPosition() {
        Xoroshiro128PositionalFactory f = new Xoroshiro128(77L).forkPositional();
        Assertions.assertEquals(f.at(1, 2, 3).nextLong(), f.at(1, 2, 3).nextLong());
        Assertions.assertNotEquals(f.at(1, 2, 3).nextLong(), f.at(3, 2, 1).nextLong());
    }
}
