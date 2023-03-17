package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockOrangeWool extends BlockWool {
    @Override
    public int getId() {
        return ORANGE_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.ORANGE.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }
}
