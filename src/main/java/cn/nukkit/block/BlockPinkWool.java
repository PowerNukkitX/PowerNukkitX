package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockPinkWool extends BlockWool {
    @Override
    public int getId() {
        return PINK_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.PINK.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}
