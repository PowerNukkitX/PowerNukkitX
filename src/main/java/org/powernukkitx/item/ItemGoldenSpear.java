package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemGoldenSpear extends ItemSpear {
    public static final ItemDefinition DEFINITION = ItemSpear.DEFINITION.toBuilder()
            .attackDamage(2)
            .maxDurability(ItemTool.DURABILITY_GOLD)
            .tier(ItemTool.TIER_GOLD)
            .build();

    public ItemGoldenSpear() {
        this(0, 1);
    }

    public ItemGoldenSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenSpear(Integer meta, int count) {
        super(GOLDEN_SPEAR, meta, count, "Golden Spear", DEFINITION);
    }
}
