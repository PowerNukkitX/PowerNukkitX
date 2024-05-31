package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockHoneycombBlock extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(HONEYCOMB_BLOCK);
    /**
     * @deprecated 
     */
    

    public BlockHoneycombBlock() {
        super(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockHoneycombBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0.6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Honeycomb Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

}
