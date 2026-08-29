package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powernukkitx.math.BlockFace;

public class TorchFacingDirectionTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(6, TorchFacingDirection.values().length);
        for (TorchFacingDirection d : TorchFacingDirection.values()) {
            Assertions.assertSame(d, TorchFacingDirection.valueOf(d.name()));
            Assertions.assertNotNull(d.getTorchDirection());
            Assertions.assertNotNull(d.getAttachedFace());
        }
    }

    @Test
    void getByTorchDirection() {
        Assertions.assertSame(TorchFacingDirection.TOP,
                TorchFacingDirection.getByTorchDirection(BlockFace.UP));
        Assertions.assertSame(TorchFacingDirection.WEST,
                TorchFacingDirection.getByTorchDirection(BlockFace.EAST));
        Assertions.assertNull(TorchFacingDirection.getByTorchDirection(BlockFace.DOWN));
    }

    @Test
    void getByAttachedFace() {
        Assertions.assertSame(TorchFacingDirection.TOP,
                TorchFacingDirection.getByAttachedFace(BlockFace.DOWN));
        Assertions.assertSame(TorchFacingDirection.EAST,
                TorchFacingDirection.getByAttachedFace(BlockFace.EAST));
        Assertions.assertNull(TorchFacingDirection.getByAttachedFace(BlockFace.UP));
    }
}
