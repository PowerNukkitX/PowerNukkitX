package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public abstract class BlockConcrete extends BlockSolid {
    /**
     * @deprecated 
     */
    
    public BlockConcrete(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 9;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
