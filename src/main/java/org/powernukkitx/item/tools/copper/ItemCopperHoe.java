package org.powernukkitx.item.tools.copper;

import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperHoe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .hoe(true)
            .maxDurability(ItemTool.DURABILITY_COPPER)
            .tier(WEARABLE_TIER_COPPER)
            .build();

    public ItemCopperHoe() {
        super(COPPER_HOE, DEFINITION);
    }
}
