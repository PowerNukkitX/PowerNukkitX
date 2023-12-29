package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author CreeperFace
 * @since 26. 11. 2016
 */
public class BlockStairsRedSandstone extends BlockStairs {

    public BlockStairsRedSandstone() {
        this(0);
    }

    public BlockStairsRedSandstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return RED_SANDSTONE_STAIRS;
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public String getName() {
        return "Red Sandstone Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
