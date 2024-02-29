package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockLightGrayCandleCake();
    }
}