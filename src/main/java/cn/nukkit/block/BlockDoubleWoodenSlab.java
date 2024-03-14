package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

public abstract class BlockDoubleWoodenSlab extends BlockDoubleSlabBase {
    public BlockDoubleWoodenSlab(BlockState blockstate) {
        super(blockstate);
    }
    @Override
    public String getName() {
        return "Double " + getSlabName() + " Wood Slab";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    protected boolean isCorrectTool(Item item) {
        return true;
    }
}