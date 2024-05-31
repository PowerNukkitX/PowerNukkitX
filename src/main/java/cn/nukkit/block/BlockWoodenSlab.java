package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public abstract class BlockWoodenSlab extends BlockSlab {
    /**
     * @deprecated 
     */
    
    public BlockWoodenSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }
    /**
     * @deprecated 
     */
    

    public BlockWoodenSlab(BlockState blockState, String doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}