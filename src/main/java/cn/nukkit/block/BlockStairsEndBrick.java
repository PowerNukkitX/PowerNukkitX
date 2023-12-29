package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockStairsEndBrick extends BlockStairs {

    public BlockStairsEndBrick() {
        this(0);
    }


    public BlockStairsEndBrick(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return END_BRICK_STAIRS;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 45;
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
        return "End Stone Brick Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
