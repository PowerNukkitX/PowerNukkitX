package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteHelmet extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(3)
            .helmet(true)
            .knockbackResistance(0.1f)
            .lavaResistant(true)
            .maxDurability(407)
            .tier(Item.WEARABLE_TIER_NETHERITE)
            .toughness(3)
            .build();

    public ItemNetheriteHelmet() {
        this(0, 1);
    }

    public ItemNetheriteHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteHelmet(Integer meta, int count) {
        super(NETHERITE_HELMET, meta, count, "Netherite Helmet", DEFINITION);
    }
}
