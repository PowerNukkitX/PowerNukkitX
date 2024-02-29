package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockLightGrayCandle();
    }
}