package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemCopperNautilusArmor extends ItemNautilusArmor {
    public static final ItemDefinition DEFINITION = ItemNautilusArmor.DEFINITION.toBuilder()
            .maxDurability(ItemTool.DURABILITY_COPPER)
            .tier(ItemTool.TIER_COPPER)
            .build();

    public ItemCopperNautilusArmor() {
        this(0, 1);
    }

    public ItemCopperNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemCopperNautilusArmor(Integer meta, int count) {
        super(COPPER_NAUTILUS_ARMOR, meta, count, "Copper Nautilus Armor", DEFINITION);
    }

}
