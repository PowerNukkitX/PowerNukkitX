package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public abstract class BlockDoubleSlabBase extends BlockSolid {
    public static final BlockDefinition DEFINITION = BlockSolid.SOLID.toBuilder()
            .hardness(2)
            .resistance(3)
            .build();

    public BlockDoubleSlabBase(BlockState blockState) {
        this(blockState, DEFINITION);
    }

    public BlockDoubleSlabBase(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public String getName() {
        return "Double " + getSlabName() + " Slab";
    }

    public abstract String getSlabName();

    public abstract BlockState getSingleSlab();

    @Override
    public Item toItem() {
        return Block.get(getSingleSlab()).toItem();
    }

    @Override
    public double getResistance() {
        return getToolType() == ItemTool.TYPE_PICKAXE ? 6 : 3;
    }

    protected boolean isCorrectTool(Item item) {
        return canHarvestWithHand() || canHarvest(item);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isCorrectTool(item)) {
            Item slab = toItem();
            slab.setCount(2);
            return new Item[]{slab};
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
