package cn.nukkit.block;

public class BlockCandleCakePink extends BlockCandleCake {
    public BlockCandleCakePink(BlockState blockstate) {
        super(blockstate);
    }

    public BlockCandleCakePink() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Pink";
    }

    @Override
    public int getId() {
        return PINK_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandlePink();
    }
}
