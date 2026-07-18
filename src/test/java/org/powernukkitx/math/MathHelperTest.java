package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class MathHelperTest {

    @Test
    void getRandomNumberInRange() {
        Random random = new Random(42);
        for (int i = 0; i < 100; i++) {
            int r = MathHelper.getRandomNumberInRange(random, 5, 10);
            Assertions.assertTrue(r >= 5 && r <= 10);
        }
        Assertions.assertEquals(7, MathHelper.getRandomNumberInRange(new Random(1), 7, 7));
    }

    @Test
    void sqrt() {
        Assertions.assertEquals(3f, MathHelper.sqrt(9f), 1e-6);
    }

    @Test
    void sinCos() {
        Assertions.assertEquals(0f, MathHelper.sin(0f), 1e-3);
        Assertions.assertEquals(1f, MathHelper.cos(0f), 1e-3);
        Assertions.assertEquals(1f, MathHelper.sin((float) (Math.PI / 2)), 1e-3);
        Assertions.assertEquals(0f, MathHelper.sin(0.0), 1e-3);
        Assertions.assertEquals(1f, MathHelper.cos(0.0), 1e-3);
    }

    @Test
    void floor() {
        Assertions.assertEquals(1, MathHelper.floor(1.9));
        Assertions.assertEquals(-2, MathHelper.floor(-1.1));
        Assertions.assertEquals(1L, MathHelper.floor_double_long(1.9));
        Assertions.assertEquals(-2L, MathHelper.floor_double_long(-1.1));
        Assertions.assertEquals(1, MathHelper.floor_float_int(1.9f));
        Assertions.assertEquals(-2, MathHelper.floor_float_int(-1.1f));
    }

    @Test
    void abs() {
        Assertions.assertEquals(5, MathHelper.abs(5));
        Assertions.assertEquals(5, MathHelper.abs(-5));
        Assertions.assertEquals(0, MathHelper.abs(0));
    }

    @Test
    void log2() {
        Assertions.assertEquals(4, MathHelper.log2(8));
        Assertions.assertEquals(1, MathHelper.log2(1));
    }

    @Test
    void max4() {
        Assertions.assertEquals(4.0, MathHelper.max(1, 2, 3, 4), 1e-9);
        Assertions.assertEquals(4.0, MathHelper.max(4, 2, 3, 1), 1e-9);
        Assertions.assertEquals(4.0, MathHelper.max(1, 4, 3, 2), 1e-9);
        Assertions.assertEquals(4.0, MathHelper.max(1, 2, 4, 3), 1e-9);
    }

    @Test
    void ceil() {
        Assertions.assertEquals(2, MathHelper.ceil(1.1f));
        Assertions.assertEquals(2, MathHelper.ceil(2.0f));
        Assertions.assertEquals(-1, MathHelper.ceil(-1.9f));
    }

    @Test
    void clamp() {
        Assertions.assertEquals(5, MathHelper.clamp(10, 0, 5));
        Assertions.assertEquals(0, MathHelper.clamp(-1, 0, 5));
        Assertions.assertEquals(3, MathHelper.clamp(3, 0, 5));
        Assertions.assertEquals(5f, MathHelper.clamp(10f, 0f, 5f), 1e-6);
        Assertions.assertEquals(0f, MathHelper.clamp(-1f, 0f, 5f), 1e-6);
    }

    @Test
    void denormalizeClamp() {
        Assertions.assertEquals(0.0, MathHelper.denormalizeClamp(0.0, 10.0, -1.0), 1e-9);
        Assertions.assertEquals(10.0, MathHelper.denormalizeClamp(0.0, 10.0, 2.0), 1e-9);
        Assertions.assertEquals(5.0, MathHelper.denormalizeClamp(0.0, 10.0, 0.5), 1e-9);
        Assertions.assertEquals(0f, MathHelper.denormalizeClamp(0f, 10f, -1f), 1e-6);
        Assertions.assertEquals(10f, MathHelper.denormalizeClamp(0f, 10f, 2f), 1e-6);
        Assertions.assertEquals(5f, MathHelper.denormalizeClamp(0f, 10f, 0.5f), 1e-6);
    }
}
