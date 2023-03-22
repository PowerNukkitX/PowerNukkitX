package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockWool extends BlockSolid {
    @Override
    public String getName() {
        return getDyeColor().getName() + " Wool";
    }

    @Override
    public int getId() {
        return WHITE_WOOL;
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
        return 4;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.WHITE.getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}
