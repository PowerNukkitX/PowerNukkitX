package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockYellowCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(YELLOW_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockYellowCandle();
    }
}