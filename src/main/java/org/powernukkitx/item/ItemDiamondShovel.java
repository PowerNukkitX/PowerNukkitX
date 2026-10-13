package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondShovel extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .shovel(true)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemDiamondShovel() {
        super(DIAMOND_SHOVEL, DEFINITION);
    }
}
