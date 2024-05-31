package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.DyeColor;

public abstract class BlockWool extends BlockSolid {
    /**
     * @deprecated 
     */
    
    public BlockWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return getDyeColor().getName() + " Wool";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 30;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 60;
    }

    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
