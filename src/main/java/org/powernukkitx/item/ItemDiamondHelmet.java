package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondHelmet extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(3)
            .helmet(true)
            .maxDurability(364)
            .tier(Item.WEARABLE_TIER_DIAMOND)
            .toughness(2)
            .build();

    public ItemDiamondHelmet() {
        super(DIAMOND_HELMET, DEFINITION);
    }
}
