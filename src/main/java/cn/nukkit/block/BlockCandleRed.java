package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleRed extends BlockCandle {

    public BlockCandleRed() { this(0); }

    public BlockCandleRed(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.RED_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeRed();
    }
}