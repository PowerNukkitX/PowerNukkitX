package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author joserobjr
 */


public class BlockBricksQuartz extends BlockSolid {


    public BlockBricksQuartz() {
        // Does nothing
    }

    @Override
    public int getId() {
        return QUARTZ_BRICKS;
    }

    @Override
    public String getName() {
        return "Quartz Bricks";
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 4;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
