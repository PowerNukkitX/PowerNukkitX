package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockQuartzBricks extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(QUARTZ_BRICKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockQuartzBricks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockQuartzBricks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Quartz Bricks";
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
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
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
    
    public boolean canHarvestWithHand() {
        return false;
    }
}