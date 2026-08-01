package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenHelmet extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(2)
            .helmet(true)
            .maxDurability(78)
            .tier(Item.WEARABLE_TIER_GOLD)
            .build();

    public ItemGoldenHelmet() {
        this(0, 1);
    }

    public ItemGoldenHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenHelmet(Integer meta, int count) {
        super(GOLDEN_HELMET, meta, count, "Golden Helmet", DEFINITION);
    }
}