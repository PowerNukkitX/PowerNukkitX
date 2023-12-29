package cn.nukkit.block;

public class BlockCandleCakeYellow extends BlockCandleCake {
    public BlockCandleCakeYellow(BlockState blockstate) {
        super(blockstate);
    }

    public BlockCandleCakeYellow() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Yellow";
    }

    @Override
    public int getId() {
        return YELLOW_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleYellow();
    }
}
