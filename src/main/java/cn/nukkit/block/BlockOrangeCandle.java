package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockOrangeCandleCake();
    }
}