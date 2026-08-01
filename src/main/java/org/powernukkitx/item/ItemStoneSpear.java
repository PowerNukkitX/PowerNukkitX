package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemStoneSpear extends ItemSpear {
    public static final ItemDefinition DEFINITION = ItemSpear.DEFINITION.toBuilder()
            .attackDamage(3)
            .maxDurability(ItemTool.DURABILITY_STONE)
            .tier(ItemTool.TIER_STONE)
            .build();

    public ItemStoneSpear() {
        this(0, 1);
    }

    public ItemStoneSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneSpear(Integer meta, int count) {
        super(STONE_SPEAR, meta, count, "Stone Spear", DEFINITION);
    }
}
