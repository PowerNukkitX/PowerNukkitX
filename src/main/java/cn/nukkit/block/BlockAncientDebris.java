package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockAncientDebris extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(ANCIENT_DEBRIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockAncientDebris() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockAncientDebris(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Ancient Derbris";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1200;
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
    
    public double getHardness() {
        return 30;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }
}
