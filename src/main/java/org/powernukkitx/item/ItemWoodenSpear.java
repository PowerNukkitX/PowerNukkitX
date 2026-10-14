package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemWoodenSpear extends ItemSpear {
    public static final ItemDefinition DEFINITION = ItemSpear.DEFINITION.toBuilder()
            .attackDamage(2)
            .maxDurability(ItemTool.DURABILITY_WOODEN)
            .tier(ItemTool.TIER_WOODEN)
            .build();

    public ItemWoodenSpear() {
        this(0, 1);
    }

    public ItemWoodenSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenSpear(Integer meta, int count) {
        super(WOODEN_SPEAR, meta, count, "Wooden Spear", DEFINITION);
    }
}
