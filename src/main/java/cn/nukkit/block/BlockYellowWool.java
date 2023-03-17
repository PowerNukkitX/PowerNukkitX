package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockYellowWool extends BlockWool {
    @Override
    public int getId() {
        return YELLOW_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.YELLOW.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}
