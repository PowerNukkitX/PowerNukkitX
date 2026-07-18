package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.math.BlockFace;

public class OrientationTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(12, Orientation.values().length);
        for (Orientation o : Orientation.values()) {
            Assertions.assertSame(o, Orientation.valueOf(o.name()));
        }
    }

    @Test
    void valuesArray() {
        Assertions.assertArrayEquals(Orientation.values(), Orientation.VALUES);
    }

    @Test
    void getByFaces() {
        Assertions.assertSame(Orientation.DOWN_EAST,
                Orientation.getByFaces(BlockFace.DOWN, BlockFace.EAST));
        Assertions.assertSame(Orientation.UP_WEST,
                Orientation.getByFaces(BlockFace.UP, BlockFace.WEST));
        Assertions.assertNull(Orientation.getByFaces(BlockFace.DOWN, BlockFace.DOWN));
    }
}
