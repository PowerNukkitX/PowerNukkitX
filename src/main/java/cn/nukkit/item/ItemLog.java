package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemLog extends Item {
    public ItemLog() {
        this(0, 1);
    }

    public ItemLog(Integer meta) {
        this(meta, 1);
    }

    public ItemLog(Integer meta, int count) {
        super(LOG, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                this.name = "Oak Log";
                setBlockUnsafe(Block.get(BlockID.OAK_LOG));
            case 1:
                this.name = "Spruce Log";
                setBlockUnsafe(Block.get(BlockID.SPRUCE_LOG));
            case 2:
                this.name = "Birch Log";
                setBlockUnsafe(Block.get(BlockID.BIRCH_LOG));
            case 3:
                this.name = "Jungle Log";
                setBlockUnsafe(Block.get(BlockID.JUNGLE_LOG));
        }
        this.meta = 0;
    }


    @Override
    public boolean equalItemBlock(Item item) {
        if (this.isBlock() && item.isBlock()) {
            return  this.getBlockUnsafe().getProperties() == item.getBlockUnsafe().getProperties();
        }
        return true;
    }
}