package org.powernukkitx.item.customitem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Custom items written against the old getter based API should keep behaving the same now that
 * their components are applied onto an {@link ItemDefinition}.
 */
class CustomItemCompatibilityTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.ITEM.init();
    }

    @Test
    void absentComponentsPreserveBaseDefinition() {
        ItemDefinition base = ItemTool.TOOL.toBuilder()
                .maxDurability(ItemTool.DURABILITY_WOODEN)
                .edible(true)
                .build();
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem(base)).build();

        assertTrue(custom.isTool());
        assertTrue(custom.isCanTakeDamage());
        assertTrue(custom.isEdible());
        assertEquals(ItemTool.DURABILITY_WOODEN, custom.getMaxDurability());
    }

    @Test
    void nonToolEnchantSlotDoesNotClearBaseToolFlag() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem(ItemTool.TOOL))
                .enchantable("bow", 5)
                .build();

        assertTrue(custom.isTool());
    }

    @Test
    void customDefinitionBacksItemGetters() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem(null))
                .damage(4)
                .build();
        TestCustomItem item = new TestCustomItem(custom);

        assertSame(custom, item.definition());
        assertEquals(4, item.getAttackDamage());
    }

    @Test
    void componentEditsAfterBuildAreNotReflected() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem(null))
                .damage(4)
                .build();
        TestCustomItem item = new TestCustomItem(custom);

        custom.getComponents().getCompound("minecraft:damage").putByte("value", 9);
        assertEquals(4, item.getAttackDamage());
    }

    @Test
    void toolClassificationUsesOverriddenPredicates() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem(null)).build();
        Item item = new TestCustomItem(custom) {
            @Override
            public boolean isPickaxe() {
                return true;
            }
        };

        assertTrue(item.isTool());
    }

    @Test
    void saturationUsesOverriddenNutrition() {
        Item item = new Item("test:overridden_food") {
            @Override
            public int getNutrition() {
                return 10;
            }

            @Override
            public CompoundTag getCustomItemComponent(String key) {
                return "minecraft:food".equals(key)
                        ? new CompoundTag().putFloat("saturation_modifier", 0.5f)
                        : null;
            }
        };

        assertEquals(10f, item.getSaturation());
    }

    private static class TestCustomItem extends Item implements CustomItem {
        private TestCustomItem(ItemDefinition definition) {
            super("test:compatibility_item", definition);
        }

        @Override
        public CustomItemDefinition getDefinition() {
            return definition() instanceof CustomItemDefinition custom ? custom : null;
        }
    }
}
