package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLUE_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockLightBlueCandle();
    }
}