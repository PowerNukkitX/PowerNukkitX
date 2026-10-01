package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondHoe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .hoe(true)
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemDiamondHoe() {
        super(DIAMOND_HOE, DEFINITION);
    }
}
