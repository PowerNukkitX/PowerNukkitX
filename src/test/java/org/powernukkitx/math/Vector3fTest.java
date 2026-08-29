package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector3fTest {

    @Test
    void constructorsAndGetters() {
        Vector3f v = new Vector3f(1.5f, 2.5f, 3.5f);
        Assertions.assertEquals(1.5f, v.getX(), 1e-6);
        Assertions.assertEquals(2.5f, v.getY(), 1e-6);
        Assertions.assertEquals(3.5f, v.getZ(), 1e-6);
        Assertions.assertEquals(0f, new Vector3f().getX(), 1e-6);
        Assertions.assertEquals(0f, new Vector3f(1).getY(), 1e-6);
        Assertions.assertEquals(0f, new Vector3f(1, 2).getZ(), 1e-6);
    }

    @Test
    void setters() {
        Vector3f v = new Vector3f();
        Assertions.assertSame(v, v.setX(7).setY(8).setZ(9));
        Assertions.assertEquals(new Vector3f(7, 8, 9), v);
    }

    @Test
    void floorGetters() {
        Vector3f v = new Vector3f(-1.2f, 2.9f, -0.1f);
        Assertions.assertEquals(-2, v.getFloorX());
        Assertions.assertEquals(2, v.getFloorY());
        Assertions.assertEquals(-1, v.getFloorZ());
    }

    @Test
    void addSubtract() {
        Vector3f a = new Vector3f(1, 2, 3);
        Assertions.assertEquals(new Vector3f(5, 7, 9), a.add(new Vector3f(4, 5, 6)));
        Assertions.assertEquals(new Vector3f(-3, -3, -3), a.subtract(new Vector3f(4, 5, 6)));
        Assertions.assertEquals(new Vector3f(1, 2, 3), a.subtract());
        Assertions.assertEquals(new Vector3f(3, 2, 3), a.add(2));
    }

    @Test
    void multiplyDivide() {
        Vector3f a = new Vector3f(2, 4, 6);
        Assertions.assertEquals(new Vector3f(4, 8, 12), a.multiply(2));
        Assertions.assertEquals(new Vector3f(1, 2, 3), a.divide(2));
    }

    @Test
    void ceilFloorRoundAbs() {
        Vector3f v = new Vector3f(1.2f, -1.7f, 2.5f);
        Assertions.assertEquals(new Vector3f(2, -1, 3), v.ceil());
        Assertions.assertEquals(new Vector3f(1, -2, 2), v.floor());
        Assertions.assertEquals(new Vector3f(1, -2, 3), v.round());
        Assertions.assertEquals(new Vector3f(1, 1, 2), v.abs());
    }

    @Test
    void sidesAndOpposite() {
        Vector3f v = new Vector3f(0, 0, 0);
        Assertions.assertEquals(new Vector3f(0, -1, 0), v.getSide(Vector3f.SIDE_DOWN));
        Assertions.assertEquals(new Vector3f(0, 1, 0), v.getSide(Vector3f.SIDE_UP));
        Assertions.assertEquals(new Vector3f(0, 0, -1), v.getSide(Vector3f.SIDE_NORTH));
        Assertions.assertEquals(new Vector3f(0, 0, 1), v.getSide(Vector3f.SIDE_SOUTH));
        Assertions.assertEquals(new Vector3f(-1, 0, 0), v.getSide(Vector3f.SIDE_WEST));
        Assertions.assertEquals(new Vector3f(1, 0, 0), v.getSide(Vector3f.SIDE_EAST));
        Assertions.assertSame(v, v.getSide(99));
        Assertions.assertEquals(Vector3f.SIDE_UP, Vector3f.getOppositeSide(Vector3f.SIDE_DOWN));
        Assertions.assertEquals(Vector3f.SIDE_WEST, Vector3f.getOppositeSide(Vector3f.SIDE_EAST));
        Assertions.assertEquals(-1, Vector3f.getOppositeSide(99));
    }

    @Test
    void distancesAndLength() {
        Vector3f a = new Vector3f(0, 0, 0);
        Vector3f b = new Vector3f(0, 3, 4);
        Assertions.assertEquals(25, a.distanceSquared(b), 1e-6);
        Assertions.assertEquals(5, a.distance(b), 1e-6);
        Assertions.assertEquals(25f, b.lengthSquared(), 1e-6);
        Assertions.assertEquals(5, b.length(), 1e-6);
    }

    @Test
    void normalizeDotCross() {
        Assertions.assertEquals(1, new Vector3f(0, 3, 4).normalize().length(), 1e-6);
        Assertions.assertEquals(0, new Vector3f(0, 0, 0).normalize().length(), 1e-6);
        Assertions.assertEquals(0f, new Vector3f(1, 0, 0).dot(new Vector3f(0, 1, 0)), 1e-6);
        Assertions.assertEquals(new Vector3f(0, 0, 1), new Vector3f(1, 0, 0).cross(new Vector3f(0, 1, 0)));
    }

    @Test
    void intermediateValues() {
        Vector3f a = new Vector3f(0, 0, 0);
        Vector3f b = new Vector3f(10, 10, 10);
        Assertions.assertEquals(new Vector3f(5, 5, 5), a.getIntermediateWithXValue(b, 5));
        Assertions.assertNull(a.getIntermediateWithXValue(b, 20));
        Assertions.assertNull(a.getIntermediateWithXValue(new Vector3f(0, 5, 5), 3));
    }

    @Test
    void setComponentsAndAxis() {
        Vector3f v = new Vector3f();
        v.setComponents(1, 2, 3);
        Assertions.assertEquals(new Vector3f(1, 2, 3), v);
        Assertions.assertEquals(1f, v.getAxis(BlockFace.Axis.X), 1e-6);
        Assertions.assertEquals(2f, v.getAxis(BlockFace.Axis.Y), 1e-6);
        Assertions.assertEquals(3f, v.getAxis(BlockFace.Axis.Z), 1e-6);
    }

    @Test
    void equalsToStringClone() {
        Vector3f v = new Vector3f(1, 2, 3);
        Assertions.assertEquals(v, new Vector3f(1, 2, 3));
        Assertions.assertNotEquals(v, new Vector3f(1, 2, 4));
        Assertions.assertNotEquals(v, "x");
        Assertions.assertEquals("Vector3(x=1.0,y=2.0,z=3.0)", v.toString());
        Vector3f c = v.clone();
        Assertions.assertEquals(v, c);
        Assertions.assertNotSame(v, c);
    }

    @Test
    void conversions() {
        Vector3f v = new Vector3f(1.2f, 2.8f, 3.5f);
        Vector3 d = v.asVector3();
        Assertions.assertEquals(1.2f, d.getX(), 1e-6);
        BlockVector3 bv = v.asBlockVector3();
        Assertions.assertEquals(1, bv.getX());
        Assertions.assertEquals(2, bv.getY());
        Assertions.assertEquals(3, bv.getZ());
        Assertions.assertEquals(1.2f, v.toHorizontal().x, 1e-6);
        Assertions.assertEquals(3.5f, v.toHorizontal().y, 1e-6);
    }

    @Test
    void directionalGetters() {
        Vector3f v = new Vector3f(1, 2, 3);
        Assertions.assertEquals(1f, v.getRight(), 1e-6);
        Assertions.assertEquals(2f, v.getUp(), 1e-6);
        Assertions.assertEquals(3f, v.getForward(), 1e-6);
        Assertions.assertEquals(1f, v.getSouth(), 1e-6);
        Assertions.assertEquals(3f, v.getWest(), 1e-6);
    }
}
