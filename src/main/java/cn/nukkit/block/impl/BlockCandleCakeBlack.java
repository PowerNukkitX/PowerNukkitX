package cn.nukkit.block.impl;

import cn.nukkit.block.impl.BlockCandle;
import cn.nukkit.block.impl.BlockCandleBlack;
import cn.nukkit.block.impl.BlockCandleCake;

public class BlockCandleCakeBlack extends BlockCandleCake {
    public BlockCandleCakeBlack(int meta) {
        super(meta);
    }

    public BlockCandleCakeBlack() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Black";
    }

    @Override
    public int getId() {
        return BLACK_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleBlack();
    }
}
