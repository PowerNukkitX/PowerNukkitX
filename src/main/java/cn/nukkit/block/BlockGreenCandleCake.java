package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGreenCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockGreenCandle();
    }
}