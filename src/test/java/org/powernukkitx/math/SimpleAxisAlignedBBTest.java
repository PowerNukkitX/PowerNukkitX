package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleAxisAlignedBBTest {

    private AxisAlignedBB unit() {
        return new SimpleAxisAlignedBB(0, 0, 0, 1, 1, 1);
    }

    @Test
    void constructorFromVectors() {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(new Vector3(2, 2, 2), new Vector3(0, 0, 0));
        Assertions.assertEquals(0, bb.getMinX(), 1e-6);
        Assertions.assertEquals(2, bb.getMaxX(), 1e-6);
    }

    @Test
    void gettersSetters() {
        AxisAlignedBB bb = unit();
        Assertions.assertEquals(0, bb.getMinX(), 1e-6);
        Assertions.assertEquals(1, bb.getMaxZ(), 1e-6);
        bb.setMinX(-1);
        bb.setMaxY(5);
        Assertions.assertEquals(-1, bb.getMinX(), 1e-6);
        Assertions.assertEquals(5, bb.getMaxY(), 1e-6);
    }

    @Test
    void setBounds() {
        AxisAlignedBB bb = unit();
        bb.setBounds(1, 2, 3, 4, 5, 6);
        Assertions.assertEquals(1, bb.getMinX(), 1e-6);
        Assertions.assertEquals(6, bb.getMaxZ(), 1e-6);
    }

    @Test
    void edgeLengthsAndVolume() {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(0, 0, 0, 2, 3, 4);
        Assertions.assertEquals(2, bb.getEdgeLengthX(), 1e-6);
        Assertions.assertEquals(3, bb.getEdgeLengthY(), 1e-6);
        Assertions.assertEquals(4, bb.getEdgeLengthZ(), 1e-6);
        Assertions.assertEquals(24, bb.getVolume(), 1e-6);
        Assertions.assertEquals(2, bb.getShortestSide(), 1e-6);
        Assertions.assertEquals(4, bb.getLongestSide(), 1e-6);
        Assertions.assertEquals(3, bb.getAverageEdgeLength(), 1e-6);
    }

    @Test
    void growShrink() {
        AxisAlignedBB grown = unit().grow(1, 1, 1);
        Assertions.assertEquals(-1, grown.getMinX(), 1e-6);
        Assertions.assertEquals(2, grown.getMaxX(), 1e-6);
        AxisAlignedBB shrunk = new SimpleAxisAlignedBB(0, 0, 0, 4, 4, 4).shrink(1, 1, 1);
        Assertions.assertEquals(1, shrunk.getMinX(), 1e-6);
        Assertions.assertEquals(3, shrunk.getMaxX(), 1e-6);
    }

    @Test
    void expandContractMutate() {
        AxisAlignedBB bb = unit();
        AxisAlignedBB same = bb.expand(1, 1, 1);
        Assertions.assertSame(bb, same);
        Assertions.assertEquals(-1, bb.getMinX(), 1e-6);
        Assertions.assertEquals(2, bb.getMaxX(), 1e-6);
        bb.contract(1, 1, 1);
        Assertions.assertEquals(0, bb.getMinX(), 1e-6);
        Assertions.assertEquals(1, bb.getMaxX(), 1e-6);
    }

    @Test
    void offset() {
        AxisAlignedBB bb = unit();
        bb.offset(1, 2, 3);
        Assertions.assertEquals(1, bb.getMinX(), 1e-6);
        Assertions.assertEquals(2, bb.getMaxX(), 1e-6);
        Assertions.assertEquals(4, bb.getMaxZ(), 1e-6);
    }

    @Test
    void getOffsetBoundingBox() {
        AxisAlignedBB bb = unit().getOffsetBoundingBox(1, 0, 0);
        Assertions.assertEquals(1, bb.getMinX(), 1e-6);
        Assertions.assertEquals(2, bb.getMaxX(), 1e-6);
        AxisAlignedBB byFace = unit().getOffsetBoundingBox(BlockFace.EAST, 2, 2, 2);
        Assertions.assertEquals(2, byFace.getMinX(), 1e-6);
    }

    @Test
    void addCoord() {
        AxisAlignedBB bb = unit().addCoord(2, -2, 0);
        Assertions.assertEquals(0, bb.getMinX(), 1e-6);
        Assertions.assertEquals(3, bb.getMaxX(), 1e-6);
        Assertions.assertEquals(-2, bb.getMinY(), 1e-6);
        Assertions.assertEquals(1, bb.getMaxY(), 1e-6);
    }

    @Test
    void setBB() {
        AxisAlignedBB bb = unit();
        bb.setBB(new SimpleAxisAlignedBB(5, 5, 5, 6, 6, 6));
        Assertions.assertEquals(5, bb.getMinX(), 1e-6);
        Assertions.assertEquals(6, bb.getMaxZ(), 1e-6);
    }

    @Test
    void intersectsWith() {
        AxisAlignedBB a = unit();
        Assertions.assertTrue(a.intersectsWith(new SimpleAxisAlignedBB(0.5, 0.5, 0.5, 1.5, 1.5, 1.5)));
        Assertions.assertFalse(a.intersectsWith(new SimpleAxisAlignedBB(2, 2, 2, 3, 3, 3)));
    }

    @Test
    void intersection() {
        AxisAlignedBB a = unit();
        AxisAlignedBB inter = a.intersection(new SimpleAxisAlignedBB(0.5, 0.5, 0.5, 2, 2, 2));
        Assertions.assertEquals(0.5, inter.getMinX(), 1e-6);
        Assertions.assertEquals(1, inter.getMaxX(), 1e-6);
    }

    @Test
    void isVectorInside() {
        AxisAlignedBB bb = unit();
        Assertions.assertTrue(bb.isVectorInside(new Vector3(0.5, 0.5, 0.5)));
        Assertions.assertFalse(bb.isVectorInside(new Vector3(2, 2, 2)));
        Assertions.assertTrue(bb.isVectorInside(0.5, 0.5, 0.5));
        Assertions.assertTrue(bb.isVectorInYZ(new Vector3(99, 0.5, 0.5)));
        Assertions.assertTrue(bb.isVectorInXZ(new Vector3(0.5, 99, 0.5)));
        Assertions.assertTrue(bb.isVectorInXY(new Vector3(0.5, 0.5, 99)));
    }

    @Test
    void corners() {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(0, 1, 2, 3, 4, 5);
        Assertions.assertEquals(new Vector3(0, 1, 2), bb.minCorner());
        Assertions.assertEquals(new Vector3(3, 4, 5), bb.maxCorner());
    }

    @Test
    void calculateOffsets() {
        AxisAlignedBB block = new SimpleAxisAlignedBB(1, 0, 0, 2, 1, 1);
        AxisAlignedBB mover = new SimpleAxisAlignedBB(0, 0, 0, 0.5, 1, 1);
        Assertions.assertEquals(0.5, block.calculateXOffset(mover, 1), 1e-6);
        AxisAlignedBB far = new SimpleAxisAlignedBB(5, 5, 5, 6, 6, 6);
        Assertions.assertEquals(1, block.calculateXOffset(far, 1), 1e-6);
    }

    @Test
    void toStringAndClone() {
        AxisAlignedBB bb = unit();
        Assertions.assertEquals("AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)", bb.toString());
        AxisAlignedBB c = bb.clone();
        Assertions.assertNotSame(bb, c);
        Assertions.assertEquals(bb.getMaxX(), c.getMaxX(), 1e-6);
    }

    @Test
    void immutableInterfaceThrows() {
        AxisAlignedBB immutable = new AxisAlignedBB() {
            @Override
            public double getMinX() { return 0; }
            @Override
            public double getMinY() { return 0; }
            @Override
            public double getMinZ() { return 0; }
            @Override
            public double getMaxX() { return 1; }
            @Override
            public double getMaxY() { return 1; }
            @Override
            public double getMaxZ() { return 1; }
            @Override
            public AxisAlignedBB clone() { return this; }
        };
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutable.setMinX(1));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutable.setMaxZ(1));
    }

    @Test
    void forEach() {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(0, 0, 0, 1.5, 1.5, 1.5);
        int[] count = {0};
        bb.forEach((x, y, z) -> count[0]++);
        Assertions.assertEquals(8, count[0]);
    }
}
