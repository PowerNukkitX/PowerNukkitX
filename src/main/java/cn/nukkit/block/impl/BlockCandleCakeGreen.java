package cn.nukkit.block.impl;

public class BlockCandleCakeGreen extends BlockCandleCake {
    public BlockCandleCakeGreen(int meta) {
        super(meta);
    }

    public BlockCandleCakeGreen() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Green";
    }

    @Override
    public int getId() {
        return GREEN_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleGreen();
    }
}
