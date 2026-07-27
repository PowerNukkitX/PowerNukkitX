package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.math.BlockFace;

public class LeverDirectionTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(8, LeverDirection.values().length);
        for (LeverDirection d : LeverDirection.values()) {
            Assertions.assertSame(d, LeverDirection.valueOf(d.name()));
        }
    }

    @Test
    void getters() {
        for (LeverDirection d : LeverDirection.values()) {
            Assertions.assertTrue(d.getMetadata() >= 0);
            Assertions.assertNotNull(d.getFacing());
            Assertions.assertNotNull(d.getName());
            Assertions.assertEquals(d.getName(), d.toString());
        }
        Assertions.assertEquals(BlockFace.EAST, LeverDirection.EAST.getFacing());
    }

    @Test
    void byMetadataRoundTrip() {
        for (LeverDirection d : LeverDirection.values()) {
            Assertions.assertSame(d, LeverDirection.byMetadata(d.getMetadata()));
        }
        Assertions.assertNotNull(LeverDirection.byMetadata(-1));
        Assertions.assertNotNull(LeverDirection.byMetadata(999));
    }

    @Test
    void forFacings() {
        Assertions.assertSame(LeverDirection.NORTH,
                LeverDirection.forFacings(BlockFace.NORTH, BlockFace.NORTH));
        Assertions.assertSame(LeverDirection.EAST,
                LeverDirection.forFacings(BlockFace.EAST, BlockFace.NORTH));
        Assertions.assertSame(LeverDirection.DOWN_EAST_WEST,
                LeverDirection.forFacings(BlockFace.DOWN, BlockFace.EAST));
        Assertions.assertSame(LeverDirection.UP_NORTH_SOUTH,
                LeverDirection.forFacings(BlockFace.UP, BlockFace.NORTH));
    }
}
