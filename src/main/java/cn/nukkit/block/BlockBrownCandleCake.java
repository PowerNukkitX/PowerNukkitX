package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockBrownCandle();
    }
}