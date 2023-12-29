package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownCandle(BlockState blockstate) {
        super(blockstate);
    }
}