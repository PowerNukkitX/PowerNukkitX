package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockLightBlueCandleCake();
    }
}