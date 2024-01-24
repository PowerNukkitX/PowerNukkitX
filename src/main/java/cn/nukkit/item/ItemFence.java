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
        adjust();
    }

    private void adjust() {
        switch (getDamage()) {
            case 0:
                this.name = "Oak Fence";
                this.block = Block.get(BlockID.OAK_FENCE);
                return;
            case 1:
                this.name = "Spruce Fence";
                this.block = Block.get(BlockID.SPRUCE_FENCE);
                return;
            case 2:
                this.name = "Birch Fence";
                this.block = Block.get(BlockID.BIRCH_FENCE);
                return;
            case 3:
                this.name = "Jungle Fence";
                this.block = Block.get(BlockID.JUNGLE_FENCE);
                return;
            case 4:
                this.name = "Acacia Fence";
                this.block = Block.get(BlockID.ACACIA_FENCE);
                return;
            case 5:
                this.name = "Dark Oak Fence";
                this.block = Block.get(BlockID.DARK_OAK_FENCE);
        }
    }
}