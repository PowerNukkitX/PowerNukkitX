package cn.nukkit.block;

public class BlockCandleCakeRed extends BlockCandleCake {
    public BlockCandleCakeRed(int meta) {
        super(meta);
    }

    public BlockCandleCakeRed() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Red";
    }

    @Override
    public int getId() {
        return RED_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleRed();
    }
}
