package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/6
 */
public class BlockIronBars extends BlockThin {

    public static final BlockProperties $1 = new BlockProperties(IRON_BARS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockIronBars() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockIronBars(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Iron Bars";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 10;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
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
