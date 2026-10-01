package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronLeggings extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(5)
            .leggings(true)
            .maxDurability(226)
            .tier(Item.WEARABLE_TIER_IRON)
            .build();

    public ItemIronLeggings() {
        this(0, 1);
    }

    public ItemIronLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemIronLeggings(Integer meta, int count) {
        super(IRON_LEGGINGS, meta, count, "Iron Leggings", DEFINITION);
    }
}