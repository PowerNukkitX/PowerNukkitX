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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Custom items written against the old getter based API should keep behaving the same now that
 * their components are folded onto an {@link ItemDefinition}.
 */
class CustomItemCompatibilityTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.ITEM.init();
    }

    @Test
    void absentComponentsPreserveBaseDefinition() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem()).build();

        ItemDefinition resolved = custom.toItemDefinition(ItemTool.TOOL.toBuilder()
                .maxDurability(ItemTool.DURABILITY_WOODEN)
                .edible(true)
                .build());

        assertTrue(resolved.isTool());
        assertTrue(resolved.isCanTakeDamage());
        assertTrue(resolved.isEdible());
        assertEquals(ItemTool.DURABILITY_WOODEN, resolved.getMaxDurability());
    }

    @Test
    void nonToolEnchantSlotDoesNotClearBaseToolFlag() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem())
                .enchantable("bow", 5)
                .build();

        assertTrue(custom.toItemDefinition(ItemTool.TOOL).isTool());
    }

    @Test
    void componentMutationsAreVisibleToExistingItems() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem())
                .damage(4)
                .build();
        TestCustomItem item = new TestCustomItem(custom);

        assertEquals(4, item.getAttackDamage());
        custom.getComponents().getCompound("minecraft:damage").putByte("value", 9);
        assertEquals(9, item.getAttackDamage());
    }

    @Test
    void toolClassificationUsesOverriddenPredicates() {
        CustomItemDefinition custom = CustomItemDefinition.simpleBuilder(new TestCustomItem()).build();
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
        private final CustomItemDefinition customDefinition;

        private TestCustomItem() {
            this(null);
        }

        private TestCustomItem(CustomItemDefinition customDefinition) {
            super("test:compatibility_item");
            this.customDefinition = customDefinition;
        }

        @Override
        public CustomItemDefinition getDefinition() {
            return customDefinition;
        }

        @Override
        public CustomItemDefinition getCustomDefinition() {
            return customDefinition;
        }
    }
}
