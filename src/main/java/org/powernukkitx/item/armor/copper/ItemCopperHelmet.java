package org.powernukkitx.item.armor.copper;

import org.powernukkitx.item.ItemArmor;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperHelmet extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(2)
            .helmet(true)
            .maxDurability(122)
            .tier(WEARABLE_TIER_COPPER)
            .toughness(2)
            .build();

    public ItemCopperHelmet() {
        super(COPPER_HELMET, DEFINITION);
    }
}
