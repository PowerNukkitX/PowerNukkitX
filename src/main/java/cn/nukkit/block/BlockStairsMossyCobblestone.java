package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockStairsMossyCobblestone extends BlockStairs {

    public BlockStairsMossyCobblestone() {
        this(0);
    }


    public BlockStairsMossyCobblestone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return MOSSY_COBBLESTONE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 2;
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
        return "Mossy Cobblestone Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
