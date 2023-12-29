package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockDoubleSlabBlackstone extends BlockDoubleSlabBase {


    public BlockDoubleSlabBlackstone() {
        this(0);
    }


    protected BlockDoubleSlabBlackstone(BlockState blockstate) {
        super(blockstate);
    }


    @Override
    public String getSlabName() {
        return "Double Blackstone Slab";
    }

    @Override
    public int getId() {
        return BLACKSTONE_DOUBLE_SLAB;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }


    @Override
    public int getSingleSlabId() {
        return BLACKSTONE_SLAB;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
