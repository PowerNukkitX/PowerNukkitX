package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCryingObsidian extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(CRYING_OBSIDIAN);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCryingObsidian() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCryingObsidian(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Crying Obsidian";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 50;
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
    
    public int getLightLevel() {
        return 10;
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
    
    public boolean canBePushed() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePulled() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

}