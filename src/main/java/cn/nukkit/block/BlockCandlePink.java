package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandlePink extends BlockCandle {

    public BlockCandlePink() { this(0); }

    public BlockCandlePink(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.PINK_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakePink();
    }
}