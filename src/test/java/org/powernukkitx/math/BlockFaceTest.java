package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;

public class BlockFaceTest {

    @Test
    void fromIndex() {
        Assertions.assertEquals(BlockFace.DOWN, BlockFace.fromIndex(0));
        Assertions.assertEquals(BlockFace.UP, BlockFace.fromIndex(1));
        Assertions.assertEquals(BlockFace.NORTH, BlockFace.fromIndex(2));
        Assertions.assertEquals(BlockFace.SOUTH, BlockFace.fromIndex(3));
        Assertions.assertEquals(BlockFace.WEST, BlockFace.fromIndex(4));
        Assertions.assertEquals(BlockFace.EAST, BlockFace.fromIndex(5));
        Assertions.assertEquals(BlockFace.DOWN, BlockFace.fromIndex(6));
    }

    @Test
    void indexGetters() {
        Assertions.assertEquals(0, BlockFace.DOWN.getIndex());
        Assertions.assertEquals(5, BlockFace.EAST.getIndex());
        Assertions.assertEquals("down", BlockFace.DOWN.getName());
        Assertions.assertEquals("east", BlockFace.EAST.getName());
        Assertions.assertEquals("east", BlockFace.EAST.toString());
    }

    @Test
    void horizontalIndex() {
        Assertions.assertEquals(0, BlockFace.SOUTH.getHorizontalIndex());
        Assertions.assertEquals(1, BlockFace.WEST.getHorizontalIndex());
        Assertions.assertEquals(2, BlockFace.NORTH.getHorizontalIndex());
        Assertions.assertEquals(3, BlockFace.EAST.getHorizontalIndex());
        Assertions.assertEquals(BlockFace.SOUTH, BlockFace.fromHorizontalIndex(0));
        Assertions.assertEquals(BlockFace.WEST, BlockFace.fromHorizontalIndex(1));
        Assertions.assertEquals(BlockFace.NORTH, BlockFace.fromHorizontalIndex(2));
        Assertions.assertEquals(BlockFace.EAST, BlockFace.fromHorizontalIndex(3));
    }

    @Test
    void horizontalAngle() {
        Assertions.assertEquals(0f, BlockFace.SOUTH.getHorizontalAngle(), 1e-6);
        Assertions.assertEquals(90f, BlockFace.WEST.getHorizontalAngle(), 1e-6);
        Assertions.assertEquals(180f, BlockFace.NORTH.getHorizontalAngle(), 1e-6);
        Assertions.assertEquals(270f, BlockFace.EAST.getHorizontalAngle(), 1e-6);
    }

    @Test
    void fromHorizontalAngle() {
        Assertions.assertEquals(BlockFace.SOUTH, BlockFace.fromHorizontalAngle(0));
        Assertions.assertEquals(BlockFace.WEST, BlockFace.fromHorizontalAngle(90));
        Assertions.assertEquals(BlockFace.NORTH, BlockFace.fromHorizontalAngle(180));
    }

    @Test
    void getOpposite() {
        Assertions.assertEquals(BlockFace.UP, BlockFace.DOWN.getOpposite());
        Assertions.assertEquals(BlockFace.DOWN, BlockFace.UP.getOpposite());
        Assertions.assertEquals(BlockFace.SOUTH, BlockFace.NORTH.getOpposite());
        Assertions.assertEquals(BlockFace.NORTH, BlockFace.SOUTH.getOpposite());
        Assertions.assertEquals(BlockFace.EAST, BlockFace.WEST.getOpposite());
        Assertions.assertEquals(BlockFace.WEST, BlockFace.EAST.getOpposite());
    }

    @Test
    void offsets() {
        Assertions.assertEquals(0, BlockFace.DOWN.getXOffset());
        Assertions.assertEquals(-1, BlockFace.DOWN.getYOffset());
        Assertions.assertEquals(1, BlockFace.UP.getYOffset());
        Assertions.assertEquals(-1, BlockFace.NORTH.getZOffset());
        Assertions.assertEquals(1, BlockFace.SOUTH.getZOffset());
        Assertions.assertEquals(-1, BlockFace.WEST.getXOffset());
        Assertions.assertEquals(1, BlockFace.EAST.getXOffset());
    }

    @Test
    void unitVector() {
        Assertions.assertEquals(new Vector3(0, -1, 0), BlockFace.DOWN.getUnitVector());
        Assertions.assertEquals(new Vector3(1, 0, 0), BlockFace.EAST.getUnitVector());
    }

    @Test
    void rotateY() {
        Assertions.assertEquals(BlockFace.EAST, BlockFace.NORTH.rotateY());
        Assertions.assertEquals(BlockFace.SOUTH, BlockFace.EAST.rotateY());
        Assertions.assertEquals(BlockFace.WEST, BlockFace.SOUTH.rotateY());
        Assertions.assertEquals(BlockFace.NORTH, BlockFace.WEST.rotateY());
        Assertions.assertThrows(IllegalStateException.class, BlockFace.UP::rotateY);
    }

    @Test
    void rotateYCCW() {
        Assertions.assertEquals(BlockFace.WEST, BlockFace.NORTH.rotateYCCW());
        Assertions.assertEquals(BlockFace.NORTH, BlockFace.EAST.rotateYCCW());
        Assertions.assertEquals(BlockFace.EAST, BlockFace.SOUTH.rotateYCCW());
        Assertions.assertEquals(BlockFace.SOUTH, BlockFace.WEST.rotateYCCW());
        Assertions.assertThrows(IllegalStateException.class, BlockFace.DOWN::rotateYCCW);
    }

    @Test
    void axis() {
        Assertions.assertEquals(BlockFace.Axis.Y, BlockFace.DOWN.getAxis());
        Assertions.assertEquals(BlockFace.Axis.Z, BlockFace.NORTH.getAxis());
        Assertions.assertEquals(BlockFace.Axis.X, BlockFace.EAST.getAxis());
        Assertions.assertTrue(BlockFace.DOWN.getAxis().isVertical());
        Assertions.assertTrue(BlockFace.NORTH.getAxis().isHorizontal());
    }

    @Test
    void axisDirection() {
        Assertions.assertEquals(BlockFace.AxisDirection.NEGATIVE, BlockFace.DOWN.getAxisDirection());
        Assertions.assertEquals(BlockFace.AxisDirection.POSITIVE, BlockFace.UP.getAxisDirection());
        Assertions.assertEquals(1, BlockFace.AxisDirection.POSITIVE.getOffset());
        Assertions.assertEquals(-1, BlockFace.AxisDirection.NEGATIVE.getOffset());
    }

    @Test
    void fromAxis() {
        Assertions.assertEquals(BlockFace.EAST, BlockFace.fromAxis(BlockFace.AxisDirection.POSITIVE, BlockFace.Axis.X));
        Assertions.assertEquals(BlockFace.WEST, BlockFace.fromAxis(BlockFace.AxisDirection.NEGATIVE, BlockFace.Axis.X));
        Assertions.assertEquals(BlockFace.UP, BlockFace.fromAxis(BlockFace.AxisDirection.POSITIVE, BlockFace.Axis.Y));
        Assertions.assertEquals(BlockFace.SOUTH, BlockFace.fromAxis(BlockFace.AxisDirection.POSITIVE, BlockFace.Axis.Z));
    }

    @Test
    void dunesw() {
        Assertions.assertEquals(0, BlockFace.DOWN.getDUNESWIndex());
        Assertions.assertEquals(1, BlockFace.UP.getDUNESWIndex());
        Assertions.assertEquals(2, BlockFace.NORTH.getDUNESWIndex());
        Assertions.assertEquals(3, BlockFace.EAST.getDUNESWIndex());
        Assertions.assertEquals(4, BlockFace.SOUTH.getDUNESWIndex());
        Assertions.assertEquals(5, BlockFace.WEST.getDUNESWIndex());
    }

    @Test
    void duswne() {
        Assertions.assertEquals(0, BlockFace.DOWN.getDUSWNEIndex());
        Assertions.assertEquals(1, BlockFace.UP.getDUSWNEIndex());
        Assertions.assertEquals(2, BlockFace.SOUTH.getDUSWNEIndex());
        Assertions.assertEquals(3, BlockFace.WEST.getDUSWNEIndex());
        Assertions.assertEquals(4, BlockFace.NORTH.getDUSWNEIndex());
        Assertions.assertEquals(5, BlockFace.EAST.getDUSWNEIndex());
    }

    @Test
    void compassRoseDirection() {
        Assertions.assertEquals(CompassRoseDirection.NORTH, BlockFace.NORTH.getCompassRoseDirection());
        Assertions.assertEquals(CompassRoseDirection.EAST, BlockFace.EAST.getCompassRoseDirection());
        Assertions.assertNull(BlockFace.UP.getCompassRoseDirection());
    }

    @Test
    void horizontalsAndRandom() {
        Assertions.assertEquals(4, BlockFace.getHorizontals().length);
        BlockFace r = BlockFace.random(new Random(1));
        Assertions.assertNotNull(r);
    }

    @Test
    void getEdges() {
        Set<BlockFace> vertical = BlockFace.UP.getEdges();
        Assertions.assertTrue(vertical.contains(BlockFace.NORTH));
        Assertions.assertTrue(vertical.contains(BlockFace.EAST));
        Assertions.assertEquals(4, vertical.size());

        Set<BlockFace> east = BlockFace.EAST.getEdges();
        Assertions.assertTrue(east.contains(BlockFace.UP));
        Assertions.assertTrue(east.contains(BlockFace.DOWN));
        Assertions.assertTrue(east.contains(BlockFace.NORTH));
        Assertions.assertTrue(east.contains(BlockFace.SOUTH));
    }

    @Test
    void axisEnum() {
        Assertions.assertEquals("x", BlockFace.Axis.X.getName());
        Assertions.assertEquals("y", BlockFace.Axis.Y.toString());
        Assertions.assertTrue(BlockFace.Axis.X.test(BlockFace.EAST));
        Assertions.assertFalse(BlockFace.Axis.X.test(BlockFace.UP));
        Assertions.assertFalse(BlockFace.Axis.X.test(null));
        Assertions.assertEquals(BlockFace.Plane.HORIZONTAL, BlockFace.Axis.X.getPlane());
        Assertions.assertEquals(BlockFace.Plane.VERTICAL, BlockFace.Axis.Y.getPlane());
    }

    @Test
    void planeEnum() {
        Assertions.assertTrue(BlockFace.Plane.HORIZONTAL.test(BlockFace.NORTH));
        Assertions.assertFalse(BlockFace.Plane.HORIZONTAL.test(BlockFace.UP));
        Assertions.assertFalse(BlockFace.Plane.HORIZONTAL.test(null));
        Assertions.assertTrue(BlockFace.Plane.VERTICAL.test(BlockFace.UP));

        int count = 0;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            count++;
            Assertions.assertTrue(face.getAxis().isHorizontal());
        }
        Assertions.assertEquals(4, count);
        Assertions.assertNotNull(BlockFace.Plane.HORIZONTAL.random());
    }

    @Test
    void axisDirectionToString() {
        Assertions.assertEquals("Towards positive", BlockFace.AxisDirection.POSITIVE.toString());
        Assertions.assertEquals("Towards negative", BlockFace.AxisDirection.NEGATIVE.toString());
    }
}
