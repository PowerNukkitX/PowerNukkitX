package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockCyanWool extends BlockWool {
    @Override
    public int getId() {
        return CYAN_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.CYAN.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}
