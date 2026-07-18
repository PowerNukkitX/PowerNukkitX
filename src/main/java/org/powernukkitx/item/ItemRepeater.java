package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;

public class ItemRepeater extends Item {
    public ItemRepeater() {
        super(REPEATER, 0, 1, "Redstone Repeater");
        this.block = Block.get(BlockID.UNPOWERED_REPEATER);
    }
}