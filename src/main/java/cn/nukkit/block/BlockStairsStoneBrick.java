package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemTool;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public class BlockStairsStoneBrick extends BlockStairs {
    public BlockStairsStoneBrick() {
        this(0);
    }

    public BlockStairsStoneBrick(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STONE_BRICK_STAIRS;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
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
    public String getName() {
        return "Stone Brick Stairs";
    }

}
