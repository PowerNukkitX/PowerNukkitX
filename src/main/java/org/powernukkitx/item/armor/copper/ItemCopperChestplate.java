package org.powernukkitx.item.armor.copper;

import org.powernukkitx.item.ItemArmor;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperChestplate extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(4)
            .chestplate(true)
            .maxDurability(177)
            .tier(WEARABLE_TIER_COPPER)
            .toughness(2)
            .build();

    public ItemCopperChestplate() {
        super(COPPER_CHESTPLATE, DEFINITION);
    }
}
