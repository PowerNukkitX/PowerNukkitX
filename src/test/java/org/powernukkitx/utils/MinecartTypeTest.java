package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MinecartTypeTest {
    @Test
    void idsAndNames() {
        Assertions.assertEquals(0, MinecartType.MINECART_EMPTY.getId());
        Assertions.assertEquals("Minecart", MinecartType.MINECART_EMPTY.getName());
        Assertions.assertEquals(1, MinecartType.MINECART_CHEST.getId());
        Assertions.assertEquals("Minecart with Chest", MinecartType.MINECART_CHEST.getName());
        Assertions.assertEquals(-1, MinecartType.MINECART_UNKNOWN.getId());
    }

    @Test
    void hasBlockInside() {
        Assertions.assertFalse(MinecartType.MINECART_EMPTY.hasBlockInside());
        Assertions.assertTrue(MinecartType.MINECART_CHEST.hasBlockInside());
        Assertions.assertFalse(MinecartType.MINECART_UNKNOWN.hasBlockInside());
    }

    @Test
    void valueOfKnown() {
        Assertions.assertEquals(MinecartType.MINECART_TNT, MinecartType.valueOf(3));
        Assertions.assertEquals(MinecartType.MINECART_HOPPER, MinecartType.valueOf(5));
    }

    @Test
    void valueOfUnknownFallsBack() {
        Assertions.assertEquals(MinecartType.MINECART_UNKNOWN, MinecartType.valueOf(999));
    }
}
