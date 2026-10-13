package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondSword extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(7)
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .sword(true)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemDiamondSword() {
        super(DIAMOND_SWORD, DEFINITION);
    }
}
