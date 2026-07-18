package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;

public class ItemBeetrootSeeds extends Item {
    public ItemBeetrootSeeds() {
        super(BEETROOT_SEEDS);
        this.block = Block.get(BlockID.BEETROOT);
    }
}