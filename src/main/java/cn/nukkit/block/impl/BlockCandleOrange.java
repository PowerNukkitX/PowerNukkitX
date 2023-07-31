package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */
public class BlockCandleOrange extends BlockCandle {

    public BlockCandleOrange() {
        this(0);
    }

    public BlockCandleOrange(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.ORANGE_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeOrange();
    }
}
