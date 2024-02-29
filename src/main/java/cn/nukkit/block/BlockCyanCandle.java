package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCyanCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(CYAN_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockCyanCandleCake();
    }
}