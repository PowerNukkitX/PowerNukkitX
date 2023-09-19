package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */
public class BlockCandleBrown extends BlockCandle {

    public BlockCandleBrown() {
        this(0);
    }

    public BlockCandleBrown(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.BROWN_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeBrown();
    }
}
