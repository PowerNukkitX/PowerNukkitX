package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronBoots extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(2)
            .boots(true)
            .maxDurability(196)
            .tier(Item.WEARABLE_TIER_IRON)
            .build();

    public ItemIronBoots() {
        this(0, 1);
    }

    public ItemIronBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemIronBoots(Integer meta, int count) {
        super(IRON_BOOTS, meta, count, "Iron Boots", DEFINITION);
    }
}