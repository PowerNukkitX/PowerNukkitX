package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public abstract class BlockConcrete extends BlockSolid {
    public BlockConcrete(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public double getHardness() {
        return 1.8;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
