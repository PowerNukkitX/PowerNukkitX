package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIME_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockLimeCandleCake();
    }
}