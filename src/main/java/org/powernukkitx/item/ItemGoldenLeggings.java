package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenLeggings extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(3)
            .leggings(true)
            .maxDurability(106)
            .tier(Item.WEARABLE_TIER_GOLD)
            .build();

    public ItemGoldenLeggings() {
        this(0, 1);
    }

    public ItemGoldenLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenLeggings(Integer meta, int count) {
        super(GOLDEN_LEGGINGS, meta, count, "Golden Leggings", DEFINITION);
    }
}