package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockStairs;
import cn.nukkit.item.ItemTool;

@PowerNukkitOnly
public class BlockStairsEndBrick extends BlockStairs {
    @PowerNukkitOnly
    public BlockStairsEndBrick() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockStairsEndBrick(int meta) {
        super(meta);
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
    @PowerNukkitOnly
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
