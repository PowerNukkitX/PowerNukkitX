package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemWaterBucket extends ItemBucket {
    public ItemWaterBucket() {
        super(WATER_BUCKET);
    }

    @Override
    public int getBucketType() {
        return 8;
    }

    @Override
    public Block getTargetBlock() {
        return Block.get(BlockID.FLOWING_WATER);
    }
}