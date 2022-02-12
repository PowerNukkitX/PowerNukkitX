package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleCyan extends BlockCandle {

    public BlockCandleCyan() { this(0); }

    public BlockCandleCyan(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.CYAN_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeCyan();
    }
}