package org.powernukkitx.block;

import org.powernukkitx.item.ItemTool;

public abstract class BlockWoolStairs extends BlockStairs {
    protected BlockWoolStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return getWoolName() + " Wool Stairs";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public double getResistance() {
        return 0.8;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    protected abstract String getWoolName();
}

