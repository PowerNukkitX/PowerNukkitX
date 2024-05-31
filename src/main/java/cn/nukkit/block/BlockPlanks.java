package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockPlanks extends BlockSolid {
    /**
     * @deprecated 
     */
    

    public BlockPlanks(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 15;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}
