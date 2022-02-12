package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleLightBlue extends  BlockCandle {

    public BlockCandleLightBlue() { this(0); }

    public BlockCandleLightBlue(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.LIGHT_BLUE_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeLightBlue();
    }
}