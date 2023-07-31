package cn.nukkit.block.impl;

import cn.nukkit.block.impl.BlockCandle;
import cn.nukkit.block.impl.BlockCandleBrown;
import cn.nukkit.block.impl.BlockCandleCake;

public class BlockCandleCakeBrown extends BlockCandleCake {
    public BlockCandleCakeBrown(int meta) {
        super(meta);
    }

    public BlockCandleCakeBrown() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Brown";
    }

    @Override
    public int getId() {
        return BROWN_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleBrown();
    }
}
