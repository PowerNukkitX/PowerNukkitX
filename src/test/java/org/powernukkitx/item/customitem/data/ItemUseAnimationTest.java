package org.powernukkitx.item.customitem.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemUseAnimationTest {

    @Test
    void roundTrip() {
        Assertions.assertTrue(ItemUseAnimation.values().length > 0);
        for (ItemUseAnimation a : ItemUseAnimation.values()) {
            Assertions.assertSame(a, ItemUseAnimation.valueOf(a.name()));
        }
    }

    @Test
    void asStringLowercase() {
        for (ItemUseAnimation a : ItemUseAnimation.values()) {
            Assertions.assertEquals(a.name().toLowerCase(), a.asString());
        }
    }

    @Test
    void getId() {
        for (ItemUseAnimation a : ItemUseAnimation.values()) {
            Assertions.assertEquals(a.ordinal() + 1, a.getId());
        }
        Assertions.assertEquals(1, ItemUseAnimation.EAT.getId());
    }
}
