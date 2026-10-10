package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteChestplate extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(8)
            .chestplate(true)
            .knockbackResistance(0.1f)
            .lavaResistant(true)
            .maxDurability(592)
            .tier(Item.WEARABLE_TIER_NETHERITE)
            .toughness(3)
            .build();

    public ItemNetheriteChestplate() {
        this(0, 1);
    }

    public ItemNetheriteChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteChestplate(Integer meta, int count) {
        super(NETHERITE_CHESTPLATE, meta, count, "Netherite Chestplate", DEFINITION);
    }
}
