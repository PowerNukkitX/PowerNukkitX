package org.powernukkitx.item.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemArmorTypeTest {

    @Test
    void roundTrip() {
        Assertions.assertTrue(ItemArmorType.values().length > 0);
        for (ItemArmorType t : ItemArmorType.values()) {
            Assertions.assertSame(t, ItemArmorType.valueOf(t.name()));
            Assertions.assertNotNull(t.id());
        }
    }

    @Test
    void fromId() {
        Assertions.assertSame(ItemArmorType.NONE, ItemArmorType.fromId(null));
        Assertions.assertSame(ItemArmorType.NONE, ItemArmorType.fromId(""));
        Assertions.assertSame(ItemArmorType.NONE, ItemArmorType.fromId("bogus"));
        for (ItemArmorType t : ItemArmorType.values()) {
            if (t == ItemArmorType.NONE) continue;
            Assertions.assertSame(t, ItemArmorType.fromId(t.id()));
        }
    }

    @Test
    void get() {
        Assertions.assertSame(ItemArmorType.HEAD, ItemArmorType.get("helmet"));
        Assertions.assertSame(ItemArmorType.CHEST, ItemArmorType.get("chestplate"));
        Assertions.assertSame(ItemArmorType.LEGS, ItemArmorType.get("leggings"));
        Assertions.assertSame(ItemArmorType.FEET, ItemArmorType.get("boots"));
        Assertions.assertSame(ItemArmorType.NONE, ItemArmorType.get(null));
        Assertions.assertSame(ItemArmorType.NONE, ItemArmorType.get("nonsense"));
        Assertions.assertSame(ItemArmorType.HEAD, ItemArmorType.get("HEAD"));
    }

    @Test
    void fromEnchantSlot() {
        Assertions.assertSame(ItemArmorType.NONE, ItemArmorType.fromEnchantSlot(null));
        Assertions.assertSame(ItemArmorType.NONE, ItemArmorType.fromEnchantSlot(ItemEnchantSlot.NONE));
        Assertions.assertSame(ItemArmorType.HEAD, ItemArmorType.fromEnchantSlot(ItemEnchantSlot.ARMOR_HEAD));
        Assertions.assertSame(ItemArmorType.CHEST, ItemArmorType.fromEnchantSlot(ItemEnchantSlot.CHESTPLATE));
        Assertions.assertSame(ItemArmorType.LEGS, ItemArmorType.fromEnchantSlot(ItemEnchantSlot.ARMOR_LEGS));
        Assertions.assertSame(ItemArmorType.FEET, ItemArmorType.fromEnchantSlot(ItemEnchantSlot.BOOTS));
    }
}
