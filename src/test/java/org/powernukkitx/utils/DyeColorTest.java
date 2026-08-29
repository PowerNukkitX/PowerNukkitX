package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.registry.Registries;

public class DyeColorTest {

    @BeforeAll
    static void setup() {
        Registries.BLOCK.init();
        Registries.ITEM.init();
    }

    @Test
    void valuesNotEmpty() {
        Assertions.assertEquals(16, DyeColor.values().length);
    }

    @Test
    void roundTrip() {
        for (DyeColor c : DyeColor.values()) {
            Assertions.assertSame(c, DyeColor.valueOf(c.name()));
        }
    }

    @Test
    void getters() {
        for (DyeColor c : DyeColor.values()) {
            Assertions.assertNotNull(c.getColor());
            Assertions.assertNotNull(c.getSignColor());
            Assertions.assertNotNull(c.getLeatherColor());
            Assertions.assertNotNull(c.getName());
            Assertions.assertNotNull(c.getDyeName());
            Assertions.assertTrue(c.getDyeData() >= 0 && c.getDyeData() <= 15);
            Assertions.assertTrue(c.getWoolData() >= 0 && c.getWoolData() <= 15);
            Assertions.assertTrue(c.getItemDyeMeta() >= 0);
        }
    }

    @Test
    void knownConstant() {
        Assertions.assertEquals(0, DyeColor.BLACK.getDyeData());
        Assertions.assertEquals(15, DyeColor.WHITE.getDyeData());
        Assertions.assertEquals("Red", DyeColor.RED.getName());
    }

    @Test
    void lookups() {
        Assertions.assertSame(DyeColor.BLACK, DyeColor.getByDyeData(0));
        Assertions.assertNotNull(DyeColor.getByDyeData(-100));
        Assertions.assertNotNull(DyeColor.getByDyeData(100));
        for (DyeColor c : DyeColor.values()) {
            Assertions.assertNotNull(DyeColor.getByWoolData(c.getWoolData()));
        }
    }
}
