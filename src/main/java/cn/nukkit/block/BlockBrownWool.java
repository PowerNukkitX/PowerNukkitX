package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockBrownWool extends BlockWool {
    @Override
    public int getId() {
        return BROWN_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BROWN.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }
}
