package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondPickaxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(5)
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .pickaxe(true)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemDiamondPickaxe() {
        super(DIAMOND_PICKAXE, DEFINITION);
    }
}
