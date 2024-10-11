package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

public abstract class BlockMushroomBlock extends BlockSolid {

    public BlockMushroomBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 0.2;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
