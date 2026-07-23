package org.powernukkitx.item.customitem.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CreativeCategoryTest {

    @Test
    void roundTrip() {
        for (CreativeCategory c : CreativeCategory.values()) {
            Assertions.assertSame(c, CreativeCategory.valueOf(c.name()));
        }
        Assertions.assertTrue(CreativeCategory.values().length > 0);
    }

    @Test
    void idRoundTrip() {
        Assertions.assertEquals(1, CreativeCategory.CONSTRUCTION.getId());
        Assertions.assertEquals(2, CreativeCategory.NATURE.getId());
        Assertions.assertEquals(3, CreativeCategory.EQUIPMENT.getId());
        Assertions.assertEquals(4, CreativeCategory.ITEMS.getId());
        Assertions.assertEquals(6, CreativeCategory.NONE.getId());

        for (CreativeCategory c : CreativeCategory.values()) {
            Assertions.assertSame(c, CreativeCategory.fromID(c.getId()));
        }
    }

    @Test
    void fromIdUnknownIsNone() {
        Assertions.assertSame(CreativeCategory.NONE, CreativeCategory.fromID(999));
    }

    @Test
    void itemCategory() {
        Assertions.assertNotNull(CreativeCategory.CONSTRUCTION.getItemCategory());
        Assertions.assertNull(CreativeCategory.NONE.getItemCategory());
    }
}
