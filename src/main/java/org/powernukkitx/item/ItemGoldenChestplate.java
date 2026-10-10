package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenChestplate extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(5)
            .chestplate(true)
            .maxDurability(113)
            .tier(Item.WEARABLE_TIER_GOLD)
            .build();

    public ItemGoldenChestplate() {
        this(0, 1);
    }

    public ItemGoldenChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenChestplate(Integer meta, int count) {
        super(GOLDEN_CHESTPLATE, meta, count, "Golden Chestplate", DEFINITION);
    }
}