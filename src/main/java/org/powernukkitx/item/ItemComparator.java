package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;

public class ItemComparator extends Item {
    public ItemComparator() {
        this(0, 1);
    }

    public ItemComparator(Integer meta, int count) {
        super(COMPARATOR, meta, count, "Redstone Comparator");
        this.block = Block.get(BlockID.UNPOWERED_COMPARATOR);
    }
}