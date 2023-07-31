package cn.nukkit.block.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

/**
 * @author Gabriel8579
 * @since 2021-08-13
 */
public class BlockCandleYellow extends BlockCandle {

    public BlockCandleYellow() {
        this(0);
    }

    public BlockCandleYellow(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.YELLOW_CANDLE;
    }

    @Override
    protected Block toCakeForm() {
        return new BlockCandleCakeYellow();
    }
}
