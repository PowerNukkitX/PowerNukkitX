package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemLeatherLeggings extends ItemColorArmor {
    public static final ItemDefinition DEFINITION = ItemColorArmor.DEFINITION.toBuilder()
            .armorPoints(2)
            .leggings(true)
            .maxDurability(76)
            .tier(Item.WEARABLE_TIER_LEATHER)
            .build();

    public ItemLeatherLeggings() {
        this(0, 1);
    }

    public ItemLeatherLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemLeatherLeggings(Integer meta, int count) {
        super(LEATHER_LEGGINGS, meta, count, DEFINITION);
    }
}
