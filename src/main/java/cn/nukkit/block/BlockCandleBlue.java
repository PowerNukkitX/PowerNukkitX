package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleBlue extends BlockCandle {

    public BlockCandleBlue() { this(0); }

    public BlockCandleBlue(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.BLUE_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeBlue();
    }
}