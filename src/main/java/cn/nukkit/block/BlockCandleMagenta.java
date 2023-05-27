package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleMagenta extends BlockCandle {

    public BlockCandleMagenta() { this(0); }

    public BlockCandleMagenta(int meta) { super(meta); }

    @Override
    public int getId() { return BlockID.MAGENTA_CANDLE; }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeMagenta();
    }

}