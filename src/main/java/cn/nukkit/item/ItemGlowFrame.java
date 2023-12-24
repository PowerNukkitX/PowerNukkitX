package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemGlowFrame extends Item {
    public ItemGlowFrame() {
        this(0, 1);
    }

    public ItemGlowFrame(Integer meta) {
        this(meta, 1);
    }

    public ItemGlowFrame(Integer meta, int count) {
        super(GLOW_FRAME, meta, count, "Glow Item Frame");
        this.block = Block.get(BlockID.GLOW_FRAME);
    }
}