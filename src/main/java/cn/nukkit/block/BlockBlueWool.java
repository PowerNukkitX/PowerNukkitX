package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockBlueWool extends BlockWool {
    @Override
    public int getId() {
        return BLUE_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BLUE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}
