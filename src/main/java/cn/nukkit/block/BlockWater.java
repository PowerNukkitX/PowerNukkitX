package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;

/**
 * alisa still water
 */
public class BlockWater extends BlockFlowingWater {
    public static final BlockProperties PROPERTIES = new BlockProperties(WATER, CommonBlockProperties.LIQUID_DEPTH);

    public BlockWater() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWater(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Still Water";
    }

    @Override
    public BlockLiquid getBlock() {
        return new BlockWater(blockstate);
    }
}
