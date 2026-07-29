package org.powernukkitx.item.tools.copper;

import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperAxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .axe(true)
            .canBreakShield(true)
            .maxDurability(ItemTool.DURABILITY_COPPER)
            .tier(WEARABLE_TIER_COPPER)
            .build();

    public ItemCopperAxe() {
        super(COPPER_AXE, DEFINITION);
    }
}
