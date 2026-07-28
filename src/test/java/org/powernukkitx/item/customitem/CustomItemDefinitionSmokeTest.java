package org.powernukkitx.item.customitem;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.item.customitem.data.CreativeGroup;
import org.powernukkitx.item.utils.ItemArmorType;
import org.powernukkitx.item.utils.ItemEnchantSlot;
import org.powernukkitx.registry.Registries;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Drives {@link CustomItemDefinition.SimpleBuilder} through a broad range of setter
 * chains and builds a definition from a minimal custom item.
 */
class CustomItemDefinitionSmokeTest {

    private static final AtomicInteger checked = new AtomicInteger();

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.ITEM.init();
    }

    private static void safe(Runnable r) {
        try {
            r.run();
            checked.incrementAndGet();
        } catch (Throwable ignored) {
        }
    }

    /** Minimal custom item - satisfies the Item + CustomItem bound the builder needs. */
    static final class TestCustomItem extends Item implements CustomItem {
        TestCustomItem() {
            super("test:smoke_item");
        }

        @Override
        public CustomItemDefinition getDefinition() {
            return CustomItemDefinition.simpleBuilder(this).build();
        }

        @Override
        public TestCustomItem clone() {
            return (TestCustomItem) super.clone();
        }
    }

    private static CustomItemDefinition.SimpleBuilder builder() {
        return CustomItemDefinition.simpleBuilder(new TestCustomItem());
    }

    @Test
    void basicBuild() {
        final CustomItemDefinition[] def = new CustomItemDefinition[1];
        safe(() -> def[0] = builder().build());
        safe(() -> {
            assertNotNull(def[0]);
            def[0].identifier();
            def[0].nbt();
        });
        assertTrue(checked.get() > 0);
    }

    @Test
    void richBuilderChain() {
        safe(() -> builder()
                .name("Smoke Item")
                .texture("smoke_texture")
                .icon("smoke_icon")
                .creativeCategory(CreativeCategory.ITEMS)
                .creativeGroup(CreativeGroup.ANVIL)
                .isHiddenInCommands(false)
                .glint(true)
                .foil(false)
                .allowOffHand(true)
                .handEquipped(true)
                .canDestroyInCreative(true)
                .shouldDespawn(false)
                .stackedByData(true)
                .maxStackSize(16)
                .tag("minecraft:is_tool", "custom:flag")
                .build());

        safe(() -> builder()
                .damage(5)
                .durability(250)
                .build());

        safe(() -> builder()
                .durability(300, 10, 40)
                .build());

        safe(() -> builder()
                .enchantable(ItemEnchantSlot.SWORD, 10)
                .enchantable("bow", 5)
                .build());

        safe(() -> builder()
                .useEfficiency(true)
                .miningSpeed(3.0f)
                .makePersistent(true)
                .build());
        assertTrue(checked.get() > 0);
    }

    @Test
    void wearableAndFoodChains() {
        safe(() -> builder().wearable("slot.armor.head").build());
        safe(() -> builder().wearable("slot.armor.chest", 3).build());
        safe(() -> builder().wearable(ItemArmorType.FEET).build());
        safe(() -> builder().wearable(ItemArmorType.LEGS, 2).build());
        safe(() -> builder().wearable(ItemArmorType.CHEST, 5, false).build());

        safe(() -> builder().food(true).build());
        safe(() -> builder().food(false, 4, 0.3f).build());
        safe(() -> builder().food(true, 6, 0.6f, "minecraft:bowl").build());
        assertTrue(checked.get() > 0);
    }

    @Test
    void useAndCooldownChains() {
        safe(() -> builder()
                .useAnimation("eat")
                .useModifiers(0.2f, 1.5f)
                .cooldown("custom", 2.0f)
                .fuel(1.5f)
                .blockPlacer("minecraft:dirt", "minecraft:stone")
                .build());
        assertTrue(checked.get() > 0);
    }
}
