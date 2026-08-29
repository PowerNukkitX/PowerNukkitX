package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WoodTypeTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(9, WoodType.values().length);
        for (WoodType t : WoodType.values()) {
            Assertions.assertSame(t, WoodType.valueOf(t.name()));
            Assertions.assertNotNull(t.getName());
        }
    }

    @Test
    void knownName() {
        Assertions.assertEquals("Oak", WoodType.OAK.getName());
        Assertions.assertEquals("Dark Oak", WoodType.DARK_OAK.getName());
    }
}
