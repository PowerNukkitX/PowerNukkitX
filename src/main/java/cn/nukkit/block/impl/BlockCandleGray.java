package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */
public class BlockCandleGray extends BlockCandle {

    public BlockCandleGray() {
        this(0);
    }

    public BlockCandleGray(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.GRAY_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeGray();
    }
}
