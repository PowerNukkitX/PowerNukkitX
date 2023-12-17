package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockDoubleSlabBlackstonePolished extends BlockDoubleSlabBase {


    public BlockDoubleSlabBlackstonePolished() {
        this(0);
    }


    public BlockDoubleSlabBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_DOUBLE_SLAB;
    }


    @Override
    public int getSingleSlabId() {
        return POLISHED_BLACKSTONE_SLAB;
    }


    @Override
    public String getSlabName() {
        return "Polished Blackstone";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6.0;
    }

}
