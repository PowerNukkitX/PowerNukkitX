package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(BROWN_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockBrownCandleCake();
    }
}