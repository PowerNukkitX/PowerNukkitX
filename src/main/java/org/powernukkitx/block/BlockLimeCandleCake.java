package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIME_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockLimeCandle();
    }
}