package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemGoldenNautilusArmor extends ItemNautilusArmor {
    public static final ItemDefinition DEFINITION = ItemNautilusArmor.DEFINITION.toBuilder()
            .maxDurability(ItemTool.DURABILITY_GOLD)
            .tier(ItemTool.TIER_GOLD)
            .build();

    public ItemGoldenNautilusArmor() {
        this(0, 1);
    }

    public ItemGoldenNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenNautilusArmor(Integer meta, int count) {
        super(GOLDEN_NAUTILUS_ARMOR, meta, count, "Golden Nautilus Armor", DEFINITION);
    }

}