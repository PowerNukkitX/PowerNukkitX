package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemCocoaBeans extends Item {
    public ItemCocoaBeans() {
        super(COCOA_BEANS);
        this.block = Block.get(BlockID.COCOA);
    }
}