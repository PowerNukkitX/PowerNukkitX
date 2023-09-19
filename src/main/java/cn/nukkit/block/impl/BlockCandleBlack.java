package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */
public class BlockCandleBlack extends BlockCandle {

    public BlockCandleBlack() {
        this(0);
    }

    public BlockCandleBlack(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.BLACK_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeBlack();
    }
}
