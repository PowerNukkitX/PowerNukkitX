package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockBlackWool extends BlockWool {
    @Override
    public int getId() {
        return BLACK_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.BLACK.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }
}
