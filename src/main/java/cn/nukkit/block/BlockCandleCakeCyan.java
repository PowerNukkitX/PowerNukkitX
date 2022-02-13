package cn.nukkit.block;

public class BlockCandleCakeCyan extends BlockCandleCake {
    public BlockCandleCakeCyan(int meta) {
        super(meta);
    }

    public BlockCandleCakeCyan() {
        this(0);
    }

    @Override
    protected String getColorName() {
        return "Cyan";
    }

    @Override
    public int getId() {
        return CYAN_CANDLE_CAKE;
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCandleCyan();
    }
}
