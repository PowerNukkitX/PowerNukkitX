package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/1
 */
public class BlockEndStone extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(END_STONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockEndStone() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockEndStone(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "End Stone";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 3;
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

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
}
