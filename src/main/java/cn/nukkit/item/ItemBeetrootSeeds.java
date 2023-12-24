package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemBeetrootSeeds extends Item {
    public ItemBeetrootSeeds() {
        super(BEETROOT_SEEDS);
        this.block = Block.get(BlockID.BEETROOT);
    }
}