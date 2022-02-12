package cn.nukkit.block;

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
