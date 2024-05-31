package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author GoodLucky777
 */
public class BlockTuff extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(TUFF);
    /**
     * @deprecated 
     */
    

    public BlockTuff() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTuff(BlockState blockState) {
        super(blockState);
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Tuff";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6;
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
