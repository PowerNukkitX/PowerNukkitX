package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteBoots extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(3)
            .boots(true)
            .knockbackResistance(0.1f)
            .lavaResistant(true)
            .maxDurability(481)
            .tier(Item.WEARABLE_TIER_NETHERITE)
            .toughness(3)
            .build();

    public ItemNetheriteBoots() {
        this(0, 1);
    }

    public ItemNetheriteBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteBoots(Integer meta, int count) {
        super(NETHERITE_BOOTS, meta, count, "Netherite Boots", DEFINITION);
    }
}
