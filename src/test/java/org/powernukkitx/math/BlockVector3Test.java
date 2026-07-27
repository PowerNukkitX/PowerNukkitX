package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlockVector3Test {

    @Test
    void constructorsAndGetters() {
        BlockVector3 v = new BlockVector3(1, 2, 3);
        Assertions.assertEquals(1, v.getX());
        Assertions.assertEquals(2, v.getY());
        Assertions.assertEquals(3, v.getZ());
        Assertions.assertEquals(0, new BlockVector3().getX());
    }

    @Test
    void setters() {
        BlockVector3 v = new BlockVector3();
        Assertions.assertSame(v, v.setX(7).setY(8).setZ(9));
        Assertions.assertEquals(new BlockVector3(7, 8, 9), v);
        v.setComponents(1, 2, 3);
        Assertions.assertEquals(new BlockVector3(1, 2, 3), v);
    }

    @Test
    void setComponentsAdding() {
        BlockVector3 v = new BlockVector3();
        v.setComponentsAdding(new Vector3(1.9, 2.9, 3.9), BlockFace.UP);
        Assertions.assertEquals(new BlockVector3(1, 3, 3), v);
    }

    @Test
    void intAddSubtract() {
        BlockVector3 a = new BlockVector3(1, 2, 3);
        Assertions.assertEquals(new BlockVector3(5, 7, 9), a.add(new BlockVector3(4, 5, 6)));
        Assertions.assertEquals(new BlockVector3(-3, -3, -3), a.subtract(new BlockVector3(4, 5, 6)));
        Assertions.assertEquals(new BlockVector3(1, 2, 3), a.subtract());
        Assertions.assertEquals(new BlockVector3(3, 2, 3), a.add(2));
    }

    @Test
    void doubleAddSubtractReturnsVector3() {
        BlockVector3 a = new BlockVector3(1, 2, 3);
        Assertions.assertEquals(new Vector3(1.5, 2, 3), a.add(0.5));
        Assertions.assertEquals(new Vector3(0.5, 2, 3), a.subtract(0.5));
        Assertions.assertEquals(new Vector3(2, 4, 6), a.add(new Vector3(1, 2, 3)));
    }

    @Test
    void multiplyDivide() {
        BlockVector3 a = new BlockVector3(2, 4, 6);
        Assertions.assertEquals(new BlockVector3(4, 8, 12), a.multiply(2));
        Assertions.assertEquals(new BlockVector3(1, 2, 3), a.divide(2));
    }

    @Test
    void sides() {
        BlockVector3 v = new BlockVector3(0, 0, 0);
        Assertions.assertEquals(new BlockVector3(0, 1, 0), v.up());
        Assertions.assertEquals(new BlockVector3(0, -1, 0), v.down());
        Assertions.assertEquals(new BlockVector3(0, 0, -1), v.north());
        Assertions.assertEquals(new BlockVector3(0, 0, 1), v.south());
        Assertions.assertEquals(new BlockVector3(1, 0, 0), v.east());
        Assertions.assertEquals(new BlockVector3(-1, 0, 0), v.west());
        Assertions.assertEquals(new BlockVector3(0, 3, 0), v.getSide(BlockFace.UP, 3));
    }

    @Test
    void distances() {
        BlockVector3 a = new BlockVector3(0, 0, 0);
        BlockVector3 b = new BlockVector3(0, 3, 4);
        Assertions.assertEquals(25, a.distanceSquared(b), 1e-6);
        Assertions.assertEquals(5, a.distance(b), 1e-6);
        Assertions.assertEquals(5, a.distance(new Vector3(0, 3, 4)), 1e-6);
    }

    @Test
    void chunkCoords() {
        BlockVector3 v = new BlockVector3(32, 48, 16);
        Assertions.assertEquals(2, v.getChunkX());
        Assertions.assertEquals(1, v.getChunkZ());
        Assertions.assertEquals(3, v.getChunkSectionY());
        Assertions.assertEquals(7, v.getChunkSectionY(true));
        Assertions.assertEquals(2, v.getChunkVector().getX());
    }

    @Test
    void getAxis() {
        BlockVector3 v = new BlockVector3(1, 2, 3);
        Assertions.assertEquals(1, v.getAxis(BlockFace.Axis.X));
        Assertions.assertEquals(2, v.getAxis(BlockFace.Axis.Y));
        Assertions.assertEquals(3, v.getAxis(BlockFace.Axis.Z));
    }

    @Test
    void equalsHashCodeToStringClone() {
        BlockVector3 v = new BlockVector3(1, 2, 3);
        Assertions.assertEquals(v, new BlockVector3(1, 2, 3));
        Assertions.assertNotEquals(v, new BlockVector3(1, 2, 4));
        Assertions.assertNotEquals(v, null);
        Assertions.assertEquals(v, v);
        Assertions.assertEquals(new BlockVector3(1, 2, 3).hashCode(), v.hashCode());
        Assertions.assertEquals("BlockPosition(level=,x=1,y=2,z=3)", v.toString());
        BlockVector3 c = v.clone();
        Assertions.assertEquals(v, c);
        Assertions.assertNotSame(v, c);
    }

    @Test
    void conversions() {
        BlockVector3 v = new BlockVector3(1, 2, 3);
        Assertions.assertEquals(new Vector3(1, 2, 3), v.asVector3());
        Assertions.assertEquals(new Vector3f(1, 2, 3), v.asVector3f());
    }
}
