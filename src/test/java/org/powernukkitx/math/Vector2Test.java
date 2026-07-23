package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector2Test {

    @Test
    void constructorsAndGetters() {
        Vector2 v = new Vector2(1.5, 2.5);
        Assertions.assertEquals(1.5, v.getX(), 1e-6);
        Assertions.assertEquals(2.5, v.getY(), 1e-6);
        Assertions.assertEquals(0, new Vector2().getX(), 1e-6);
        Assertions.assertEquals(0, new Vector2(3).getY(), 1e-6);
        Assertions.assertEquals(0, Vector2.ZERO.getX(), 1e-6);
    }

    @Test
    void floorGetters() {
        Vector2 v = new Vector2(-1.2, 2.9);
        Assertions.assertEquals(-2, v.getFloorX());
        Assertions.assertEquals(2, v.getFloorY());
    }

    @Test
    void addSubtract() {
        Vector2 a = new Vector2(1, 2);
        Vector2 r = a.add(new Vector2(3, 4));
        Assertions.assertEquals(4, r.x, 1e-6);
        Assertions.assertEquals(6, r.y, 1e-6);
        Vector2 s = a.subtract(new Vector2(1, 1));
        Assertions.assertEquals(0, s.x, 1e-6);
        Assertions.assertEquals(1, s.y, 1e-6);
        Assertions.assertEquals(3, a.add(2).x, 1e-6);
    }

    @Test
    void ceilFloorRoundAbs() {
        Vector2 v = new Vector2(1.2, -1.7);
        // ceil is (int)(x+1)
        Assertions.assertEquals(2, v.ceil().x, 1e-6);
        Assertions.assertEquals(0, v.ceil().y, 1e-6);
        Assertions.assertEquals(1, v.floor().x, 1e-6);
        Assertions.assertEquals(-2, v.floor().y, 1e-6);
        Assertions.assertEquals(1, v.round().x, 1e-6);
        Assertions.assertEquals(-2, v.round().y, 1e-6);
        Assertions.assertEquals(1.2, v.abs().x, 1e-6);
        Assertions.assertEquals(1.7, v.abs().y, 1e-6);
    }

    @Test
    void multiplyDivide() {
        Vector2 a = new Vector2(2, 4);
        Assertions.assertEquals(4, a.multiply(2).x, 1e-6);
        Assertions.assertEquals(2, a.divide(2).y, 1e-6);
    }

    @Test
    void distanceAndLength() {
        Vector2 a = new Vector2(0, 0);
        Vector2 b = new Vector2(3, 4);
        Assertions.assertEquals(25, a.distanceSquared(b), 1e-6);
        Assertions.assertEquals(5, a.distance(b), 1e-6);
        Assertions.assertEquals(25, b.lengthSquared(), 1e-6);
        Assertions.assertEquals(5, b.length(), 1e-6);
        Assertions.assertEquals(3, a.distance(3), 1e-6);
    }

    @Test
    void normalizeAndDot() {
        Vector2 v = new Vector2(3, 4);
        Assertions.assertEquals(1, v.normalize().length(), 1e-6);
        Assertions.assertEquals(0, new Vector2(0, 0).normalize().length(), 1e-6);
        Assertions.assertEquals(11, new Vector2(1, 2).dot(new Vector2(3, 4)), 1e-6);
    }

    @Test
    void toStringTest() {
        Assertions.assertEquals("Vector2(x=1.0,y=2.0)", new Vector2(1, 2).toString());
    }
}
