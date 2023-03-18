package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockLightBlueWool extends BlockWool {
    @Override
    public int getId() {
        return LIGHT_BLUE_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.LIGHT_BLUE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }
}
