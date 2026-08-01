package org.powernukkitx.item.armor.copper;

import org.powernukkitx.item.ItemArmor;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperLeggings extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(3)
            .leggings(true)
            .maxDurability(166)
            .tier(WEARABLE_TIER_COPPER)
            .toughness(2)
            .build();

    public ItemCopperLeggings() {
        super(COPPER_LEGGINGS, DEFINITION);
    }
}
