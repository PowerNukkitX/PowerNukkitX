package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLACK_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockBlackCandleCake();
    }
}