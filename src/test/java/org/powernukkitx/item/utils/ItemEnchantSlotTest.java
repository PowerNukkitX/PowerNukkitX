package org.powernukkitx.item.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemEnchantSlotTest {

    @Test
    void roundTrip() {
        Assertions.assertTrue(ItemEnchantSlot.values().length > 0);
        for (ItemEnchantSlot s : ItemEnchantSlot.values()) {
            Assertions.assertSame(s, ItemEnchantSlot.valueOf(s.name()));
            Assertions.assertNotNull(s.id());
        }
    }

    @Test
    void fromId() {
        Assertions.assertSame(ItemEnchantSlot.NONE, ItemEnchantSlot.fromId(null));
        Assertions.assertSame(ItemEnchantSlot.NONE, ItemEnchantSlot.fromId(""));
        Assertions.assertSame(ItemEnchantSlot.NONE, ItemEnchantSlot.fromId("bogus"));
        Assertions.assertSame(ItemEnchantSlot.SWORD, ItemEnchantSlot.fromId("sword"));
        Assertions.assertSame(ItemEnchantSlot.SWORD, ItemEnchantSlot.fromId("SWORD"));
        Assertions.assertSame(ItemEnchantSlot.PICKAXE, ItemEnchantSlot.fromId(" pickaxe "));
    }
}
