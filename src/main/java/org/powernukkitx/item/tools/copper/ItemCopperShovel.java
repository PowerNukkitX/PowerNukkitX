package org.powernukkitx.item.tools.copper;

import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperShovel extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_COPPER)
            .shovel(true)
            .tier(WEARABLE_TIER_COPPER)
            .build();

    public ItemCopperShovel() {
        super(COPPER_SHOVEL, DEFINITION);
    }
}
