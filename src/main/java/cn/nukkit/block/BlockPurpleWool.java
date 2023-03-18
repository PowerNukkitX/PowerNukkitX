package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockPurpleWool extends BlockWool {
    @Override
    public int getId() {
        return PURPLE_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.PURPLE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }
}
