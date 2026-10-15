package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteLeggings extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(6)
            .knockbackResistance(0.1f)
            .lavaResistant(true)
            .leggings(true)
            .maxDurability(555)
            .tier(Item.WEARABLE_TIER_NETHERITE)
            .toughness(3)
            .build();

    public ItemNetheriteLeggings() {
        this(0, 1);
    }

    public ItemNetheriteLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteLeggings(Integer meta, int count) {
        super(NETHERITE_LEGGINGS, meta, count, "Netherite Leggings", DEFINITION);
    }
}
