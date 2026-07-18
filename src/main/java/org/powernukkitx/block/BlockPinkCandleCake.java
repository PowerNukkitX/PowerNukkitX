package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockPinkCandle();
    }
}