package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChunkVector2Test {

    @Test
    void constructorsAndGetters() {
        ChunkVector2 v = new ChunkVector2(1, 2);
        Assertions.assertEquals(1, v.getX());
        Assertions.assertEquals(2, v.getZ());
        Assertions.assertEquals(0, new ChunkVector2().getX());
        Assertions.assertEquals(0, new ChunkVector2(5).getZ());
    }

    @Test
    void setters() {
        ChunkVector2 v = new ChunkVector2();
        v.setX(3);
        v.setZ(4);
        Assertions.assertEquals(3, v.getX());
        Assertions.assertEquals(4, v.getZ());
    }

    @Test
    void addSubtract() {
        ChunkVector2 a = new ChunkVector2(1, 2);
        ChunkVector2 r = a.add(new ChunkVector2(3, 4));
        Assertions.assertEquals(4, r.getX());
        Assertions.assertEquals(6, r.getZ());
        ChunkVector2 s = a.subtract(new ChunkVector2(1, 1));
        Assertions.assertEquals(0, s.getX());
        Assertions.assertEquals(1, s.getZ());
        Assertions.assertEquals(3, a.add(2).getX());
    }

    @Test
    void absMultiplyDivide() {
        Assertions.assertEquals(2, new ChunkVector2(-2, -3).abs().getX());
        Assertions.assertEquals(3, new ChunkVector2(-2, -3).abs().getZ());
        ChunkVector2 a = new ChunkVector2(2, 4);
        Assertions.assertEquals(4, a.multiply(2).getX());
        Assertions.assertEquals(2, a.divide(2).getZ());
    }

    @Test
    void distanceAndLength() {
        ChunkVector2 a = new ChunkVector2(0, 0);
        ChunkVector2 b = new ChunkVector2(3, 4);
        Assertions.assertEquals(25, a.distanceSquared(b), 1e-6);
        Assertions.assertEquals(5, a.distance(b), 1e-6);
        Assertions.assertEquals(25, b.lengthSquared());
        Assertions.assertEquals(5, b.length(), 1e-6);
        Assertions.assertEquals(3, a.distance(3), 1e-6);
    }

    @Test
    void dotAndToString() {
        Assertions.assertEquals(11, new ChunkVector2(1, 2).dot(new ChunkVector2(3, 4)));
        Assertions.assertEquals("MutableChunkVector(x=1,z=2)", new ChunkVector2(1, 2).toString());
    }
}
