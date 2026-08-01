package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemIronSpear extends ItemSpear {
    public static final ItemDefinition DEFINITION = ItemSpear.DEFINITION.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_IRON)
            .tier(ItemTool.TIER_IRON)
            .build();

    public ItemIronSpear() {
        this(0, 1);
    }

    public ItemIronSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemIronSpear(Integer meta, int count) {
        super(IRON_SPEAR, meta, count, "Iron Spear", DEFINITION);
    }
}