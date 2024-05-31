package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public abstract class BlockDoubleWoodenSlab extends BlockDoubleSlabBase {
    /**
     * @deprecated 
     */
    
    public BlockDoubleWoodenSlab(BlockState blockstate) {
        super(blockstate);
    }
    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Double " + getSlabName() + " Wood Slab";
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
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected boolean isCorrectTool(Item item) {
        return true;
    }
}