package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteCandle(BlockState blockstate) {
        super(blockstate);
    }
}