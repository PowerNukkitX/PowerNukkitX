package org.powernukkitx.item.customitem.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreativeGroupTest {

    @Test
    void roundTrip() {
        Assertions.assertTrue(CreativeGroup.values().length > 0);
        for (CreativeGroup g : CreativeGroup.values()) {
            Assertions.assertSame(g, CreativeGroup.valueOf(g.name()));
            Assertions.assertNotNull(g.getGroupName());
        }
    }

    @Test
    void noneIsEmpty() {
        Assertions.assertEquals("", CreativeGroup.NONE.getGroupName());
    }

    @Test
    void knownConstant() {
        Assertions.assertEquals("itemGroup.name.anvil", CreativeGroup.ANVIL.getGroupName());
    }
}
