package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCyanCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanCandle(BlockState blockstate) {
        super(blockstate);
    }
}