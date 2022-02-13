package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandlePurple extends BlockCandle {

    public BlockCandlePurple() { this(0); }

    public BlockCandlePurple(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.PURPLE_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakePurple();
    }
}