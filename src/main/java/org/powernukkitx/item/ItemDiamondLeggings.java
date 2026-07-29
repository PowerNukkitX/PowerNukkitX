package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondLeggings extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(6)
            .leggings(true)
            .maxDurability(496)
            .tier(Item.WEARABLE_TIER_DIAMOND)
            .toughness(2)
            .build();

    public ItemDiamondLeggings() {
        super(DIAMOND_LEGGINGS, DEFINITION);
    }
}
