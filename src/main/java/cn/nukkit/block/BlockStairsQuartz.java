package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemTool;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public class BlockStairsQuartz extends BlockStairs {
    public BlockStairsQuartz() {
        this(0);
    }

    public BlockStairsQuartz(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return QUARTZ_STAIRS;
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
        return "Quartz Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
