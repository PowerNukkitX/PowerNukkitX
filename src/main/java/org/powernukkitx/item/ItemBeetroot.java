package org.powernukkitx.item;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBeetroot extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(1)
            .saturation(1.2f)
            .build();

    public ItemBeetroot() {
        this(0, 1);
    }

    public ItemBeetroot(Integer meta) {
        this(meta, 1);
    }

    public ItemBeetroot(Integer meta, int count) {
        super(BlockID.BEETROOT, meta, count, "Beetroot", DEFINITION);
    }

}
