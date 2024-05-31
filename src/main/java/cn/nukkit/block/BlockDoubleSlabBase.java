package cn.nukkit.block;

import cn.nukkit.item.Item;

public abstract class BlockDoubleSlabBase extends BlockSolid {
    /**
     * @deprecated 
     */
    
    public BlockDoubleSlabBase(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Double "+getSlabName()+" Slab";
    }

    public abstract String getSlabName();

    public abstract String getSingleSlabId();

    @Override
    public Item toItem() {
        return Block.get(getSingleSlabId()).toItem();
    }

    
    /**
     * @deprecated 
     */
    protected boolean isCorrectTool(Item item) {
        return canHarvestWithHand() || canHarvest(item);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isCorrectTool(item)) {
            Item $1 = toItem();
            slab.setCount(2);
            return new Item[]{ slab };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
