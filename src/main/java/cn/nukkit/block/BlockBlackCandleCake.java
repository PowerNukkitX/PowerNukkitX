package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACK_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockBlackCandle();
    }
}