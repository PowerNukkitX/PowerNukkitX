package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public abstract class BlockWoodenSlab extends BlockSlab {
    public BlockWoodenSlab(BlockState blockState, BlockState doubleSlab) {
        super(blockState, doubleSlab);
    }

    public BlockWoodenSlab(BlockState blockState, String doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public String getName() {
        return (isOnTop() ? "Upper " : "") + getSlabName() + " Wood Slab";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }
}