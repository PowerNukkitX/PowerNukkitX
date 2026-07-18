package org.powernukkitx.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompassRoseDirectionTest {

    @Test
    void modXZ() {
        Assertions.assertEquals(0, CompassRoseDirection.NORTH.getModX());
        Assertions.assertEquals(-1, CompassRoseDirection.NORTH.getModZ());
        Assertions.assertEquals(1, CompassRoseDirection.EAST.getModX());
        Assertions.assertEquals(1, CompassRoseDirection.NORTH_EAST.getModX());
        Assertions.assertEquals(-1, CompassRoseDirection.NORTH_EAST.getModZ());
    }

    @Test
    void closestBlockFaceAndIndex() {
        Assertions.assertEquals(BlockFace.NORTH, CompassRoseDirection.NORTH.getClosestBlockFace());
        Assertions.assertEquals(BlockFace.EAST, CompassRoseDirection.EAST.getClosestBlockFace());
        Assertions.assertEquals(8, CompassRoseDirection.NORTH.getIndex());
        Assertions.assertEquals(0, CompassRoseDirection.SOUTH.getIndex());
        Assertions.assertEquals(12, CompassRoseDirection.EAST.getIndex());
        Assertions.assertEquals(4, CompassRoseDirection.WEST.getIndex());
    }

    @Test
    void yaw() {
        Assertions.assertEquals(0f, CompassRoseDirection.NORTH.getYaw(), 1e-6);
        Assertions.assertEquals(90f, CompassRoseDirection.EAST.getYaw(), 1e-6);
        Assertions.assertEquals(180f, CompassRoseDirection.SOUTH.getYaw(), 1e-6);
        Assertions.assertEquals(292.5f, CompassRoseDirection.WEST_NORTH_WEST.getYaw(), 1e-3);
    }

    @Test
    void from() {
        Assertions.assertEquals(CompassRoseDirection.SOUTH, CompassRoseDirection.from(0));
        Assertions.assertEquals(CompassRoseDirection.NORTH, CompassRoseDirection.from(8));
        Assertions.assertEquals(CompassRoseDirection.EAST, CompassRoseDirection.from(12));
        Assertions.assertEquals(CompassRoseDirection.WEST, CompassRoseDirection.from(4));
    }

    @Test
    void getOppositeFace() {
        Assertions.assertEquals(CompassRoseDirection.SOUTH, CompassRoseDirection.NORTH.getOppositeFace());
        Assertions.assertEquals(CompassRoseDirection.NORTH, CompassRoseDirection.SOUTH.getOppositeFace());
        Assertions.assertEquals(CompassRoseDirection.WEST, CompassRoseDirection.EAST.getOppositeFace());
        Assertions.assertEquals(CompassRoseDirection.EAST, CompassRoseDirection.WEST.getOppositeFace());
        Assertions.assertEquals(CompassRoseDirection.SOUTH_WEST, CompassRoseDirection.NORTH_EAST.getOppositeFace());
        Assertions.assertEquals(CompassRoseDirection.NORTH_EAST, CompassRoseDirection.SOUTH_WEST.getOppositeFace());
        for (CompassRoseDirection d : CompassRoseDirection.values()) {
            Assertions.assertEquals(d, d.getOppositeFace().getOppositeFace());
        }
    }

    @Test
    void getClosestFromYaw() {
        Assertions.assertEquals(CompassRoseDirection.NORTH, CompassRoseDirection.getClosestFromYaw(0));
        Assertions.assertEquals(CompassRoseDirection.SOUTH, CompassRoseDirection.getClosestFromYaw(180));
        Assertions.assertEquals(CompassRoseDirection.NORTH,
                CompassRoseDirection.getClosestFromYaw(0, CompassRoseDirection.Precision.CARDINAL));
    }
}
