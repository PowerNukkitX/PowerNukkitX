package cn.nukkit.block;

public class BlockCandleCakeLime extends BlockCandleCake {
    public BlockCandleCakeLime(BlockState blockstate) {
        super(blockstate);
    }

    public BlockCandleCakeLime() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Lime";
    }

    @Override
    public int getId() {
        return LIME_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleLime();
    }
}
