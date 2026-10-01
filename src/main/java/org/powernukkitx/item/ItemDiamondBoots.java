package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondBoots extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(3)
            .boots(true)
            .maxDurability(430)
            .tier(Item.WEARABLE_TIER_DIAMOND)
            .toughness(2)
            .build();

    public ItemDiamondBoots() {
        super(DIAMOND_BOOTS, DEFINITION);
    }
}
