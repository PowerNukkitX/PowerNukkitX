package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemCopperSpear extends ItemSpear {
    public static final ItemDefinition DEFINITION = ItemSpear.DEFINITION.toBuilder()
            .attackDamage(3)
            .maxDurability(ItemTool.DURABILITY_COPPER)
            .tier(ItemTool.TIER_COPPER)
            .build();

    public ItemCopperSpear() {
        this(0, 1);
    }

    public ItemCopperSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemCopperSpear(Integer meta, int count) {
        super(COPPER_SPEAR, meta, count, "Copper Spear", DEFINITION);
    }
}
