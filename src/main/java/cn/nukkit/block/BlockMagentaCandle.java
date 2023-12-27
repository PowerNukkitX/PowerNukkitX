package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaCandle(BlockState blockstate) {
        super(blockstate);
    }
}