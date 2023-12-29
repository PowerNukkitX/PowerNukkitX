package cn.nukkit.block;

public class BlockCandleCakePurple extends BlockCandleCake {
    public BlockCandleCakePurple(BlockState blockstate) {
        super(blockstate);
    }

    public BlockCandleCakePurple() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Purple";
    }

    @Override
    public int getId() {
        return PURPLE_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandlePurple();
    }
}
