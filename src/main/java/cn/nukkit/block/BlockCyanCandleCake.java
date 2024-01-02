package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCyanCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(CYAN_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockCyanCandle();
    }
}