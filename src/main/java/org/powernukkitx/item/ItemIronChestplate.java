package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronChestplate extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(6)
            .chestplate(true)
            .maxDurability(241)
            .tier(Item.WEARABLE_TIER_IRON)
            .build();

    public ItemIronChestplate() {
        this(0, 1);
    }

    public ItemIronChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemIronChestplate(Integer meta, int count) {
        super(IRON_CHESTPLATE, meta, count, "Iron Chestplate", DEFINITION);
    }
}