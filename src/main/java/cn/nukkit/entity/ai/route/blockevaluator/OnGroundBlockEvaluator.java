package cn.nukkit.entity.ai.route.blockevaluator;

import cn.nukkit.block.Block;

public class OnGroundBlockEvaluator implements IBlockEvaluator {
    @Override
    public int evalBlock(Block block) {
        if (block.getId() == Block.FLOWING_LAVA || block.getId() == Block.STILL_LAVA || block.getId() == Block.CACTUS)
            return -1;
        if (block.getId() == Block.FLOWING_WATER || block.getId() == Block.STILL_WATER)
            return 10;//排除水
        if (block.canPassThrough())
            return -1;
        return 1;
    }
}
