package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemLeatherHelmet extends ItemColorArmor {
    public static final ItemDefinition DEFINITION = ItemColorArmor.DEFINITION.toBuilder()
            .armorPoints(1)
            .helmet(true)
            .maxDurability(56)
            .tier(Item.WEARABLE_TIER_LEATHER)
            .build();

    public ItemLeatherHelmet() {
        this(0, 1);
    }

    public ItemLeatherHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherHelmet(Integer meta, int count) {
        super(LEATHER_HELMET, meta, count, DEFINITION);
    }
}
