package cn.nukkit.block;

public class BlockCandleCakeBlue extends BlockCandleCake {
    public BlockCandleCakeBlue(int meta) {
        super(meta);
    }

    public BlockCandleCakeBlue() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Blue";
    }

    @Override
    public int getId() {
        return BLUE_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleBlue();
    }
}
