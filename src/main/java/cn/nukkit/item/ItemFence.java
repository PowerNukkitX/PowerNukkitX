package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemFence extends Item {
    public ItemFence() {
        this(0);
    }

    public ItemFence(Integer meta) {
        this(meta, 1);
    }

    public ItemFence(Integer meta, int count) {
        super(FENCE, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                this.name = "Oak Fence";
                setBlockUnsafe(Block.get(BlockID.OAK_FENCE));
                return;
            case 1:
                this.name = "Spruce Fence";
                setBlockUnsafe(Block.get(BlockID.SPRUCE_FENCE));
                this.meta = 0;
                return;
            case 2:
                this.name = "Birch Fence";
                setBlockUnsafe(Block.get(BlockID.BIRCH_FENCE));
                this.meta = 0;
                return;
            case 3:
                this.name = "Jungle Fence";
                setBlockUnsafe(Block.get(BlockID.JUNGLE_FENCE));
                this.meta = 0;
                return;
            case 4:
                this.name = "Acacia Fence";
                setBlockUnsafe(Block.get(BlockID.ACACIA_FENCE));
                this.meta = 0;
                return;
            case 5:
                this.name = "Dark Oak Fence";
                setBlockUnsafe(Block.get(BlockID.DARK_OAK_FENCE));
                this.meta = 0;
        }
    }
}