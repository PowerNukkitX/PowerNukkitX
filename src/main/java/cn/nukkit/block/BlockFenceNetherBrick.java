package cn.nukkit.block;

import cn.nukkit.item.ItemTool;

/**
 * @author xtypr
 * @since 2015/12/7
 */

public class BlockFenceNetherBrick extends BlockFenceBase {

    public BlockFenceNetherBrick() {
        this(0);
    }

    public BlockFenceNetherBrick(int meta) {
        super(meta);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Nether Brick Fence";
    }

    @Override
    public int getId() {
        return NETHER_BRICK_FENCE;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }


    @Override
    public int getBurnChance() {
        return 0;
    }


    @Override
    public int getBurnAbility() {
        return 0;
    }
}
