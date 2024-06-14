package cn.nukkit.block;

import cn.nukkit.item.Item;

public abstract class BlockDoubleSlabBase extends BlockSolid {
    public BlockDoubleSlabBase(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Double "+getSlabName()+" Slab";
    }

    public abstract String getSlabName();

    public abstract BlockState getSingleSlab();

    @Override
    public Item toItem() {
        return Block.get(getSingleSlab()).toItem();
    }

    protected boolean isCorrectTool(Item item) {
        return canHarvestWithHand() || canHarvest(item);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isCorrectTool(item)) {
            Item slab = toItem();
            slab.setCount(2);
            return new Item[]{ slab };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
