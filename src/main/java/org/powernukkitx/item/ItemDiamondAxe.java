package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondAxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(6)
            .axe(true)
            .canBreakShield(true)
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemDiamondAxe() {
        super(DIAMOND_AXE, DEFINITION);
    }
}
