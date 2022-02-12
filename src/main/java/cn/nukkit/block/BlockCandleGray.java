package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleGray extends BlockCandle {

    public BlockCandleGray() { this(0); }

    public BlockCandleGray(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.GRAY_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeGray();
    }
}