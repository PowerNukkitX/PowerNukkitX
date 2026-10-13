package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronHelmet extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(2)
            .helmet(true)
            .maxDurability(166)
            .tier(Item.WEARABLE_TIER_IRON)
            .build();

    public ItemIronHelmet() {
        this(0, 1);
    }

    public ItemIronHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemIronHelmet(Integer meta, int count) {
        super(IRON_HELMET, meta, count, "Iron Helmet", DEFINITION);
    }
}