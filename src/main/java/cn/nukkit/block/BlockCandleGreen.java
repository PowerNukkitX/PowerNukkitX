package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleGreen extends BlockCandle {

    public BlockCandleGreen() { this(0); }

    public BlockCandleGreen(int meta) { super(meta); }

    @Override
    public int getId() {
        return GREEN_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeGreen();
    }
}