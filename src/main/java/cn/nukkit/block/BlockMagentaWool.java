package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockMagentaWool extends BlockWool {
    @Override
    public int getId() {
        return MAGENTA_WOOL;
    }

    @Override
    public BlockColor getColor() {
        return DyeColor.MAGENTA.getColor();
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }
}
