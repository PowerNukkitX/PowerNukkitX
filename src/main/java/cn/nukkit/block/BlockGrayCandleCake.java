package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockGrayCandle();
    }
}