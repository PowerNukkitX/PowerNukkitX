package cn.nukkit.block;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */


public class BlockCandleLightGray extends BlockCandle {

    public BlockCandleLightGray() { this(0); }

    public BlockCandleLightGray(int meta) { super(meta); }

    @Override
    public int getId() {
        return BlockID.LIGHT_GRAY_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeLightGray();
    }

}