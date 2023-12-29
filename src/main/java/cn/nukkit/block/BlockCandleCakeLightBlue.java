package cn.nukkit.block;

public class BlockCandleCakeLightBlue extends BlockCandleCake {
    public BlockCandleCakeLightBlue(BlockState blockstate) {
        super(blockstate);
    }

    public BlockCandleCakeLightBlue() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "LightBlue";
    }

    @Override
    public int getId() {
        return LIGHT_BLUE_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleLightBlue();
    }
}
