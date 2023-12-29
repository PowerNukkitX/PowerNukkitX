package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockStairsDiorite extends BlockStairs {

    public BlockStairsDiorite() {
        this(0);
    }


    public BlockStairsDiorite(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return DIORITE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public String getName() {
        return "Diorite Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
