package org.powernukkitx.item.tools.copper;

import org.powernukkitx.item.ItemTool;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperPickaxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_COPPER)
            .pickaxe(true)
            .tier(WEARABLE_TIER_COPPER)
            .build();

    public ItemCopperPickaxe() {
        super(COPPER_PICKAXE, DEFINITION);
    }
}
