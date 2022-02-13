package cn.nukkit.block;

public class BlockCandleCakeWhite extends BlockCandleCake {
    public BlockCandleCakeWhite(int meta) {
        super(meta);
    }

    public BlockCandleCakeWhite() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "White";
    }

    @Override
    public int getId() {
        return WHITE_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleWhite();
    }
}
