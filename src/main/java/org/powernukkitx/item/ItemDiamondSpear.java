package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemDiamondSpear extends ItemSpear {
    public static final ItemDefinition DEFINITION = ItemSpear.DEFINITION.toBuilder()
            .attackDamage(5)
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemDiamondSpear() {
        this(0, 1);
    }

    public ItemDiamondSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemDiamondSpear(Integer meta, int count) {
        super(DIAMOND_SPEAR, meta, count, "Diamond Spear", DEFINITION);
    }
}
