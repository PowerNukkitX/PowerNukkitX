package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class NukkitMathTest {

    @Test
    void isZero() {
        Assertions.assertTrue(NukkitMath.isZero(0));
        Assertions.assertTrue(NukkitMath.isZero(0L));
        Assertions.assertTrue(NukkitMath.isZero((byte) 0));
        Assertions.assertTrue(NukkitMath.isZero((short) 0));
        Assertions.assertTrue(NukkitMath.isZero(BigInteger.ZERO));
        Assertions.assertFalse(NukkitMath.isZero(1));
        Assertions.assertFalse(NukkitMath.isZero(1.5));
    }

    @Test
    void floorCeil() {
        Assertions.assertEquals(1, NukkitMath.floorDouble(1.9));
        Assertions.assertEquals(-2, NukkitMath.floorDouble(-1.1));
        Assertions.assertEquals(2, NukkitMath.ceilDouble(1.1));
        Assertions.assertEquals(-1, NukkitMath.ceilDouble(-1.9));
        Assertions.assertEquals(1, NukkitMath.floorFloat(1.9f));
        Assertions.assertEquals(-2, NukkitMath.floorFloat(-1.1f));
        Assertions.assertEquals(2, NukkitMath.ceilFloat(1.1f));
        Assertions.assertEquals(5, NukkitMath.floorDouble(5.0));
        Assertions.assertEquals(5, NukkitMath.ceilDouble(5.0));
    }

    @Test
    void round() {
        Assertions.assertEquals(1.0, NukkitMath.round(1.4), 1e-9);
        Assertions.assertEquals(2.0, NukkitMath.round(1.5), 1e-9);
        Assertions.assertEquals(1.23, NukkitMath.round(1.23456, 2), 1e-9);
        Assertions.assertEquals(1.2346, NukkitMath.round(1.23456, 4), 1e-9);
    }

    @Test
    void clamp() {
        Assertions.assertEquals(5.0, NukkitMath.clamp(10.0, 0.0, 5.0), 1e-9);
        Assertions.assertEquals(0.0, NukkitMath.clamp(-1.0, 0.0, 5.0), 1e-9);
        Assertions.assertEquals(3.0, NukkitMath.clamp(3.0, 0.0, 5.0), 1e-9);
        Assertions.assertEquals(5, NukkitMath.clamp(10, 0, 5));
        Assertions.assertEquals(5f, NukkitMath.clamp(10f, 0f, 5f), 1e-6);
        Assertions.assertEquals(5L, NukkitMath.clamp(10L, 0L, 5L));
        Assertions.assertEquals(0L, NukkitMath.clamp(-1L, 0L, 5L));
        Assertions.assertEquals("m", NukkitMath.clamp("z", "a", "m"));
        Assertions.assertEquals("a", NukkitMath.clamp("A", "a", "m"));
        Assertions.assertEquals("b", NukkitMath.clamp("b", "a", "m"));
    }

    @Test
    void remap() {
        Assertions.assertEquals(5f, NukkitMath.remap(0.5f, 0f, 1f, 0f, 10f), 1e-6);
        Assertions.assertEquals(0f, NukkitMath.remapNormalized(0.5f, 0f, 1f), 1e-6);
        Assertions.assertEquals(0.5f, NukkitMath.remapFromNormalized(0f, 0f, 1f), 1e-6);
    }

    @Test
    void lerp() {
        Assertions.assertEquals(0.0, NukkitMath.lerp(0, 0, 0, 10, 10), 1e-9);
        Assertions.assertEquals(10.0, NukkitMath.lerp(10, 0, 0, 10, 10), 1e-9);
        Assertions.assertEquals(5.0, NukkitMath.lerp(5, 0, 0, 10, 10), 1e-9);
    }

    @Test
    void getDirection() {
        Assertions.assertEquals(5, NukkitMath.getDirection(-3, 5), 1e-9);
        Assertions.assertEquals(3, NukkitMath.getDirection(3, -2), 1e-9);
    }

    @Test
    void bitLength() {
        Assertions.assertEquals(1, NukkitMath.bitLength((byte) 0));
        Assertions.assertEquals(32, NukkitMath.bitLength((byte) -1));
        Assertions.assertEquals(4, NukkitMath.bitLength((byte) 8));
        Assertions.assertEquals(1, NukkitMath.bitLength(0));
        Assertions.assertEquals(32, NukkitMath.bitLength(-1));
        Assertions.assertEquals(4, NukkitMath.bitLength(8));
        Assertions.assertEquals(1, NukkitMath.bitLength(0L));
        Assertions.assertEquals(64, NukkitMath.bitLength(-1L));
        Assertions.assertEquals(4, NukkitMath.bitLength(8L));
        Assertions.assertEquals(4, NukkitMath.bitLength(BigInteger.valueOf(8)));
    }

    @Test
    void bitLengthNegativeBigIntegerThrows() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> NukkitMath.bitLength(BigInteger.valueOf(-1)));
    }

    @Test
    void powerOf2() {
        Assertions.assertTrue(NukkitMath.isPowerOf2(8));
        Assertions.assertFalse(NukkitMath.isPowerOf2(6));
        Assertions.assertTrue(NukkitMath.isPowerOf2(BigInteger.valueOf(8)));
        Assertions.assertFalse(NukkitMath.isPowerOf2(BigInteger.valueOf(6)));
        Assertions.assertEquals(8L, NukkitMath.getPow2(3));
        Assertions.assertEquals(BigInteger.valueOf(8), NukkitMath.getBigPow2(3));
    }

    @Test
    void masks() {
        Assertions.assertEquals(7L, NukkitMath.getMask(3));
        Assertions.assertEquals(~0L, NukkitMath.getMask(64));
        Assertions.assertEquals(BigInteger.valueOf(7), NukkitMath.getBigMask(3));
        Assertions.assertEquals(5L, NukkitMath.mask(0b1101, 3));
        Assertions.assertEquals(BigInteger.valueOf(5), NukkitMath.bigMask(BigInteger.valueOf(0b1101), 3));
        Assertions.assertEquals(-1L, NukkitMath.maskSigned(0b1111, 4));
    }

    @Test
    void modInverse() {
        long v = 3L;
        Assertions.assertEquals(1L, v * NukkitMath.modInverse(v));
        long v2 = 7L;
        Assertions.assertEquals(1L, v2 * NukkitMath.modInverse(v2, 64));
    }

    @Test
    void minMax() {
        Assertions.assertEquals(1, NukkitMath.min(3, 1, 2));
        Assertions.assertEquals(3, NukkitMath.max(3, 1, 2));
        Assertions.assertEquals(1f, NukkitMath.min(3f, 1f, 2f), 1e-6);
        Assertions.assertEquals(3f, NukkitMath.max(3f, 1f, 2f), 1e-6);
        Assertions.assertEquals(1L, NukkitMath.min(3L, 1L, 2L));
        Assertions.assertEquals(3L, NukkitMath.max(3L, 1L, 2L));
        Assertions.assertEquals(1.0, NukkitMath.min(3.0, 1.0, 2.0), 1e-9);
        Assertions.assertEquals(3.0, NukkitMath.max(3.0, 1.0, 2.0), 1e-9);
        Assertions.assertEquals("a", NukkitMath.getMin("b", "a", "c"));
        Assertions.assertEquals("c", NukkitMath.getMax("b", "a", "c"));
    }
}
