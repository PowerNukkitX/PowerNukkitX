package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockDoubleSlabMangrove extends BlockDoubleSlabBase {


    public BlockDoubleSlabMangrove() {
        this(0);
    }


    protected BlockDoubleSlabMangrove(int meta) {
        super(meta);
    }


    @Override
    public String getSlabName() {
        return "Double Mangrove Slab";
    }

    @Override
    public int getId() {
        return DOUBLE_MANGROVE_SLAB;
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
    public int getSingleSlabId() {
        return MANGROVE_SLAB;
    }

}
