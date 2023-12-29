package cn.nukkit.block;

public class BlockCandleCakeBrown extends BlockCandleCake {
    public BlockCandleCakeBrown(BlockState blockstate) {
        super(blockstate);
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
