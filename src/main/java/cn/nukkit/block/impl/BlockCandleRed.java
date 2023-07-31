package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */
public class BlockCandleRed extends BlockCandle {

    public BlockCandleRed() {
        this(0);
    }

    public BlockCandleRed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.RED_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeRed();
    }
}
