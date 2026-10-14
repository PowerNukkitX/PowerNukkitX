package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenBoots extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(1)
            .boots(true)
            .maxDurability(92)
            .tier(Item.WEARABLE_TIER_GOLD)
            .build();

    public ItemGoldenBoots() {
        this(0, 1);
    }

    public ItemGoldenBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenBoots(Integer meta, int count) {
        super(GOLDEN_BOOTS, meta, count, "Golden Boots", DEFINITION);
    }
}