package cn.nukkit.block.impl;

public class BlockCandleCakeMagenta extends BlockCandleCake {
    public BlockCandleCakeMagenta(int meta) {
        super(meta);
    }

    public BlockCandleCakeMagenta() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Magenta";
    }

    @Override
    public int getId() {
        return MAGENTA_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleMagenta();
    }
}
