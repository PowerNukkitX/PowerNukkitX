package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector3Test {

    @Test
    void constructorsAndGetters() {
        Vector3 v = new Vector3(1.5, 2.5, 3.5);
        Assertions.assertEquals(1.5, v.getX(), 1e-6);
        Assertions.assertEquals(2.5, v.getY(), 1e-6);
        Assertions.assertEquals(3.5, v.getZ(), 1e-6);

        Assertions.assertEquals(0, new Vector3().getX(), 1e-6);
        Vector3 vx = new Vector3(4);
        Assertions.assertEquals(4, vx.getX(), 1e-6);
        Assertions.assertEquals(0, vx.getY(), 1e-6);
        Vector3 vxy = new Vector3(4, 5);
        Assertions.assertEquals(0, vxy.getZ(), 1e-6);
    }

    @Test
    void constants() {
        Assertions.assertEquals(new Vector3(0, 0, 0), Vector3.ZERO);
        Assertions.assertEquals(new Vector3(0.5, 0.5, 0.5), Vector3.HALF);
        Assertions.assertEquals(new Vector3(1, 1, 1), Vector3.ONE);
    }

    @Test
    void setters() {
        Vector3 v = new Vector3();
        Assertions.assertSame(v, v.setX(7).setY(8).setZ(9));
        Assertions.assertEquals(new Vector3(7, 8, 9), v);
    }

    @Test
    void floorGetters() {
        Vector3 v = new Vector3(-1.2, 2.9, -0.1);
        Assertions.assertEquals(-2, v.getFloorX());
        Assertions.assertEquals(2, v.getFloorY());
        Assertions.assertEquals(-1, v.getFloorZ());
    }

    @Test
    void chunkCoords() {
        Vector3 v = new Vector3(32, 48, 16);
        Assertions.assertEquals(2, v.getChunkX());
        Assertions.assertEquals(1, v.getChunkZ());
        Assertions.assertEquals(3, v.getChunkSectionY());
        Assertions.assertEquals(2, v.getChunkVector().getX());
        Assertions.assertEquals(1, v.getChunkVector().getZ());
    }

    @Test
    void addSubtract() {
        Vector3 a = new Vector3(1, 2, 3);
        Vector3 b = new Vector3(4, 5, 6);
        Assertions.assertEquals(new Vector3(5, 7, 9), a.add(b));
        Assertions.assertEquals(new Vector3(-3, -3, -3), a.subtract(b));
        Assertions.assertEquals(a, a.add(b).subtract(b));
        Assertions.assertEquals(new Vector3(3, 2, 3), a.add(2));
        Assertions.assertEquals(new Vector3(3, 4, 3), a.add(2, 2));
    }

    @Test
    void multiplyDivide() {
        Vector3 a = new Vector3(2, 4, 6);
        Assertions.assertEquals(new Vector3(4, 8, 12), a.multiply(2));
        Assertions.assertEquals(new Vector3(1, 2, 3), a.divide(2));
    }

    @Test
    void ceilFloorRoundAbs() {
        Vector3 v = new Vector3(1.2, -1.7, 2.5);
        Assertions.assertEquals(new Vector3(2, -1, 3), v.ceil());
        Assertions.assertEquals(new Vector3(1, -2, 2), v.floor());
        Assertions.assertEquals(new Vector3(1, -2, 3), v.round());
        Assertions.assertEquals(new Vector3(1, 1, 2), v.abs());
    }

    @Test
    void distances() {
        Vector3 a = new Vector3(0, 0, 0);
        Vector3 b = new Vector3(3, 4, 0);
        Assertions.assertEquals(25, a.distanceSquared(b), 1e-6);
        Assertions.assertEquals(5, a.distance(b), 1e-6);
        Assertions.assertEquals(7, a.distanceManhattan(b));
    }

    @Test
    void lengthAndNormalize() {
        Vector3 v = new Vector3(0, 3, 4);
        Assertions.assertEquals(25, v.lengthSquared(), 1e-6);
        Assertions.assertEquals(5, v.length(), 1e-6);
        Vector3 n = v.normalize();
        Assertions.assertEquals(1, n.length(), 1e-6);
        Assertions.assertEquals(new Vector3(0, 0, 0), new Vector3(0, 0, 0).normalize());
    }

    @Test
    void dotAndCross() {
        Vector3 a = new Vector3(1, 0, 0);
        Vector3 b = new Vector3(0, 1, 0);
        Assertions.assertEquals(0, a.dot(b), 1e-6);
        Assertions.assertEquals(new Vector3(0, 0, 1), a.cross(b));
    }

    @Test
    void sides() {
        Vector3 v = new Vector3(0, 0, 0);
        Assertions.assertEquals(new Vector3(0, 1, 0), v.up());
        Assertions.assertEquals(new Vector3(0, -1, 0), v.down());
        Assertions.assertEquals(new Vector3(0, 0, -1), v.north());
        Assertions.assertEquals(new Vector3(0, 0, 1), v.south());
        Assertions.assertEquals(new Vector3(1, 0, 0), v.east());
        Assertions.assertEquals(new Vector3(-1, 0, 0), v.west());
        Assertions.assertEquals(new Vector3(0, 2, 0), v.getSide(BlockFace.UP, 2));
    }

    @Test
    void maxPlainDistance() {
        Vector3 v = new Vector3(3, 0, -5);
        Assertions.assertEquals(5, v.maxPlainDistance(), 1e-6);
        Assertions.assertEquals(5, v.maxPlainDistance(new Vector3(0, 0, 0)), 1e-6);
    }

    @Test
    void intermediateValues() {
        Vector3 a = new Vector3(0, 0, 0);
        Vector3 b = new Vector3(10, 10, 10);
        Assertions.assertEquals(new Vector3(5, 5, 5), a.getIntermediateWithXValue(b, 5));
        Assertions.assertEquals(new Vector3(5, 5, 5), a.getIntermediateWithYValue(b, 5));
        Assertions.assertEquals(new Vector3(5, 5, 5), a.getIntermediateWithZValue(b, 5));
        Assertions.assertNull(a.getIntermediateWithXValue(b, 20));
        Assertions.assertNull(a.getIntermediateWithXValue(new Vector3(0, 5, 5), 3));
    }

    @Test
    void setComponents() {
        Vector3 v = new Vector3();
        v.setComponents(1, 2, 3);
        Assertions.assertEquals(new Vector3(1, 2, 3), v);
        v.setComponents(new Vector3(4, 5, 6));
        Assertions.assertEquals(new Vector3(4, 5, 6), v);
        v.setComponentsAdding(1, 1, 1, 2, 2, 2);
        Assertions.assertEquals(new Vector3(3, 3, 3), v);
    }

    @Test
    void getAxis() {
        Vector3 v = new Vector3(1, 2, 3);
        Assertions.assertEquals(1, v.getAxis(BlockFace.Axis.X), 1e-6);
        Assertions.assertEquals(2, v.getAxis(BlockFace.Axis.Y), 1e-6);
        Assertions.assertEquals(3, v.getAxis(BlockFace.Axis.Z), 1e-6);
    }

    @Test
    void equalsHashCodeToStringClone() {
        Vector3 v = new Vector3(1, 2, 3);
        Assertions.assertEquals(v, new Vector3(1, 2, 3));
        Assertions.assertNotEquals(v, new Vector3(1, 2, 4));
        Assertions.assertNotEquals(v, "x");
        Assertions.assertEquals(new Vector3(1, 2, 3).hashCode(), v.hashCode());
        Assertions.assertEquals("Vector3(x=1.0,y=2.0,z=3.0)", v.toString());
        Vector3 c = v.clone();
        Assertions.assertEquals(v, c);
        Assertions.assertNotSame(v, c);
    }

    @Test
    void conversions() {
        Vector3 v = new Vector3(1.2, 2.8, 3.5);
        Vector3f f = v.asVector3f();
        Assertions.assertEquals(1.2f, f.getX(), 1e-6);
        BlockVector3 bv = v.asBlockVector3();
        Assertions.assertEquals(1, bv.getX());
        Assertions.assertEquals(2, bv.getY());
        Assertions.assertEquals(3, bv.getZ());
        Vector2 h = v.toHorizontal();
        Assertions.assertEquals(1.2, h.x, 1e-6);
        Assertions.assertEquals(3.5, h.y, 1e-6);
    }
}
