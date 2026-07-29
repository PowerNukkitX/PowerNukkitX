package org.powernukkitx.item.armor.copper;

import org.powernukkitx.item.ItemArmor;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperBoots extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(1)
            .boots(true)
            .maxDurability(143)
            .tier(WEARABLE_TIER_COPPER)
            .toughness(2)
            .build();

    public ItemCopperBoots() {
        super(COPPER_BOOTS, DEFINITION);
    }
}
