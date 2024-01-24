package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.DyeColor;

public abstract class BlockWool extends BlockSolid {
    public BlockWool(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " Wool";
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

    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
