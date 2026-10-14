package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemDiamondNautilusArmor extends ItemNautilusArmor {
    public static final ItemDefinition DEFINITION = ItemNautilusArmor.DEFINITION.toBuilder()
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemDiamondNautilusArmor() {
        this(0, 1);
    }

    public ItemDiamondNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemDiamondNautilusArmor(Integer meta, int count) {
        super(DIAMOND_NAUTILUS_ARMOR, meta, count, "Diamond Nautilus Armor", DEFINITION);
    }

}
