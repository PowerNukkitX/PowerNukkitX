package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCarrot extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(3)
            .saturation(4.8f)
            .build();

    public ItemCarrot() {
        this(0, 1);
    }

    public ItemCarrot(Integer meta) {
        this(meta, 1);
    }

    public ItemCarrot(Integer meta, int count) {
        super(CARROT, 0, count, "Carrot", DEFINITION);
        this.block = Block.get(BlockID.CARROTS);
    }
}
