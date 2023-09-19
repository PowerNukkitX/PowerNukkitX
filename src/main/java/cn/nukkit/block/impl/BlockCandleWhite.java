package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */
public class BlockCandleWhite extends BlockCandle {

    public BlockCandleWhite() {
        this(0);
    }

    public BlockCandleWhite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.WHITE_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeWhite();
    }
}
