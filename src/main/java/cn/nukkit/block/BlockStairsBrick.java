package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemTool;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public class BlockStairsBrick extends BlockStairs {
    public BlockStairsBrick() {
        this(0);
    }

    public BlockStairsBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BRICK_STAIRS;
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
        return "Brick Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
