package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinition;
import cn.nukkit.item.ItemTool;

public abstract class BlockWoodenSlab extends BlockSlab {
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_AXE)
            .canHarvestWithHand(true)
            .toolTier(ItemTool.TYPE_NONE)
            .burnChance(5)
            .burnAbility(20)
            .build();

    public BlockWoodenSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab, DEFINITION);
    }

    public BlockWoodenSlab(BlockState blockState, String doubleSlab) {
        super(blockState, doubleSlab, DEFINITION);
    }

    public BlockWoodenSlab(BlockState blockState, BlockState doubleSlab, BlockDefinition definition) {
        super(blockState, doubleSlab, definition);
    }

    public BlockWoodenSlab(BlockState blockState, String doubleSlab, BlockDefinition definition) {
        super(blockState, doubleSlab, definition);
    }

    @Override
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }
}