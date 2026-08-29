package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.math.BlockFace;

public class MinecraftCardinalDirectionTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(4, MinecraftCardinalDirection.values().length);
        for (MinecraftCardinalDirection d : MinecraftCardinalDirection.values()) {
            Assertions.assertSame(d, MinecraftCardinalDirection.valueOf(d.name()));
        }
    }

    @Test
    void valuesArray() {
        Assertions.assertArrayEquals(MinecraftCardinalDirection.values(),
                MinecraftCardinalDirection.VALUES);
    }

    @Test
    void fromBlockFace() {
        Assertions.assertSame(MinecraftCardinalDirection.SOUTH,
                MinecraftCardinalDirection.fromBlockFace(BlockFace.SOUTH));
        Assertions.assertSame(MinecraftCardinalDirection.WEST,
                MinecraftCardinalDirection.fromBlockFace(BlockFace.WEST));
        Assertions.assertSame(MinecraftCardinalDirection.NORTH,
                MinecraftCardinalDirection.fromBlockFace(BlockFace.NORTH));
        Assertions.assertSame(MinecraftCardinalDirection.EAST,
                MinecraftCardinalDirection.fromBlockFace(BlockFace.EAST));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> MinecraftCardinalDirection.fromBlockFace(BlockFace.UP));
    }
}
