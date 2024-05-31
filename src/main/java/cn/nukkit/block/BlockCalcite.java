package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCalcite extends BlockSolid {

    public static final BlockProperties $1 = new BlockProperties(CALCITE);
    /**
     * @deprecated 
     */
    

    public BlockCalcite() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCalcite(BlockState blockState) {
        super(blockState);
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Calcite";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.75;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.75;
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
