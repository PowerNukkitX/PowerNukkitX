package org.powernukkitx.item.tools.copper;

import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperSword extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_COPPER)
            .sword(true)
            .tier(WEARABLE_TIER_COPPER)
            .build();

    public ItemCopperSword() {
        super(COPPER_SWORD, DEFINITION);
    }
}
