package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector2fTest {

    @Test
    void constructorsAndGetters() {
        Vector2f v = new Vector2f(1.5f, 2.5f);
        Assertions.assertEquals(1.5f, v.getX(), 1e-6);
        Assertions.assertEquals(2.5f, v.getY(), 1e-6);
        Assertions.assertEquals(0f, new Vector2f().getX(), 1e-6);
        Assertions.assertEquals(0f, new Vector2f(3).getY(), 1e-6);
    }

    @Test
    void floorGetters() {
        Vector2f v = new Vector2f(-1.2f, 2.9f);
        Assertions.assertEquals(-2, v.getFloorX());
        Assertions.assertEquals(2, v.getFloorY());
    }

    @Test
    void addSubtract() {
        Vector2f a = new Vector2f(1, 2);
        Vector2f r = a.add(new Vector2f(3, 4));
        Assertions.assertEquals(4f, r.x, 1e-6);
        Assertions.assertEquals(6f, r.y, 1e-6);
        Vector2f s = a.subtract(new Vector2f(1, 1));
        Assertions.assertEquals(0f, s.x, 1e-6);
        Assertions.assertEquals(1f, s.y, 1e-6);
    }

    @Test
    void ceilFloorRoundAbs() {
        Vector2f v = new Vector2f(1.2f, -1.7f);
        Assertions.assertEquals(2f, v.ceil().x, 1e-6);
        Assertions.assertEquals(0f, v.ceil().y, 1e-6);
        Assertions.assertEquals(1f, v.floor().x, 1e-6);
        Assertions.assertEquals(-2f, v.floor().y, 1e-6);
        Assertions.assertEquals(1f, v.round().x, 1e-6);
        Assertions.assertEquals(-2f, v.round().y, 1e-6);
        Assertions.assertEquals(1.7f, v.abs().y, 1e-6);
    }

    @Test
    void multiplyDivide() {
        Vector2f a = new Vector2f(2, 4);
        Assertions.assertEquals(4f, a.multiply(2).x, 1e-6);
        Assertions.assertEquals(2f, a.divide(2).y, 1e-6);
    }

    @Test
    void distanceAndLength() {
        Vector2f a = new Vector2f(0, 0);
        Vector2f b = new Vector2f(3, 4);
        Assertions.assertEquals(25, a.distanceSquared(b), 1e-6);
        Assertions.assertEquals(5, a.distance(b), 1e-6);
        Assertions.assertEquals(25f, b.lengthSquared(), 1e-6);
        Assertions.assertEquals(5, b.length(), 1e-6);
    }

    @Test
    void normalizeAndDot() {
        Vector2f v = new Vector2f(3, 4);
        Assertions.assertEquals(1, v.normalize().length(), 1e-6);
        Assertions.assertEquals(0, new Vector2f(0, 0).normalize().length(), 1e-6);
        Assertions.assertEquals(11f, new Vector2f(1, 2).dot(new Vector2f(3, 4)), 1e-6);
    }

    @Test
    void toStringTest() {
        Assertions.assertEquals("Vector2(x=1.0,y=2.0)", new Vector2f(1, 2).toString());
    }
}
