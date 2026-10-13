package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemLeatherBoots extends ItemColorArmor {
    public static final ItemDefinition DEFINITION = ItemColorArmor.DEFINITION.toBuilder()
            .armorPoints(1)
            .boots(true)
            .maxDurability(66)
            .tier(Item.WEARABLE_TIER_LEATHER)
            .build();

    public ItemLeatherBoots() {
        this(0, 1);
    }

    public ItemLeatherBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherBoots(Integer meta, int count) {
        super(LEATHER_BOOTS, meta, count, "Leather Boots", DEFINITION);
    }
}