package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemFrame extends Item {
    public ItemFrame() {
        this(0, 1);
    }

    public ItemFrame(Integer meta) {
        this(meta, 1);
    }

    public ItemFrame(Integer meta, int count) {
        super(FRAME, meta, count, "Item Frame");
        this.block = Block.get(BlockID.FRAME);
    }
}