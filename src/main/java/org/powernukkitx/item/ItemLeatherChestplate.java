package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemLeatherChestplate extends ItemColorArmor {
    public static final ItemDefinition DEFINITION = ItemColorArmor.DEFINITION.toBuilder()
            .armorPoints(3)
            .chestplate(true)
            .maxDurability(81)
            .tier(Item.WEARABLE_TIER_LEATHER)
            .build();

    public ItemLeatherChestplate() {
        this(0, 1);
    }

    public ItemLeatherChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherChestplate(Integer meta, int count) {
        super(LEATHER_CHESTPLATE, meta, count, DEFINITION);
    }
}