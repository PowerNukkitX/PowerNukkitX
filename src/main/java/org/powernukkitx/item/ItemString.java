package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemString extends Item {

    public ItemString() {
        this(0, 1);
    }

    public ItemString(Integer meta) {
        this(meta, 1);
    }

    public ItemString(Integer meta, int count) {
        super(STRING, meta, count, "String");
        this.block = Block.get(BlockID.TRIP_WIRE);
    }
}
