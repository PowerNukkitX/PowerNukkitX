package cn.nukkit.block;

public class BlockCandleCakeOrange extends BlockCandleCake {
    public BlockCandleCakeOrange(int meta) {
        super(meta);
    }

    public BlockCandleCakeOrange() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Orange";
    }

    @Override
    public int getId() {
        return ORANGE_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleOrange();
    }
}
