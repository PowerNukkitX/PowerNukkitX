package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockLightGrayCandleCake();
    }
}