package org.powernukkitx.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.registry.Registries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Values the migrated items reported through their removed getter overrides, now read from their
 * {@link org.powernukkitx.item.definition.ItemDefinition}.
 */
class ItemDefinitionParityTest {

    @BeforeAll
    static void init() {
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
    }

    @Test
    void diamondSword() {
        Item item = new ItemDiamondSword();

        assertTrue(item.isTool());
        assertTrue(item.isSword());
        assertTrue(item.canTakeDamage());
        assertEquals(1, item.getMaxStackSize());
        assertEquals(7, item.getAttackDamage());
        assertEquals(ItemTool.DURABILITY_DIAMOND, item.getMaxDurability());
        assertEquals(ItemTool.TIER_DIAMOND, item.getTier());
    }

    @Test
    void diamondHelmet() {
        Item item = new ItemDiamondHelmet();

        assertTrue(item.isArmor());
        assertTrue(item.isHelmet());
        assertFalse(item.isTool());
        assertEquals(1, item.getMaxStackSize());
        assertEquals(3, item.getArmorPoints());
        assertEquals(2, item.getToughness());
        assertEquals(364, item.getMaxDurability());
        assertEquals(Item.WEARABLE_TIER_DIAMOND, item.getTier());
    }

    @Test
    void cod() {
        Item item = new ItemCod();

        assertTrue(item.isEdible());
        assertEquals(2, item.getNutrition());
        assertEquals(0.4f, item.getSaturation());
        assertEquals(31, item.getUsingTicks());
    }

    @Test
    void shield() {
        Item item = new ItemShield();

        assertTrue(item.isShield());
        assertEquals(1, item.getMaxStackSize());
        assertEquals(ItemTool.DURABILITY_SHIELD, item.getMaxDurability());
    }

    @Test
    void egg() {
        assertEquals(16, new ItemEgg().getMaxStackSize());
    }
}
