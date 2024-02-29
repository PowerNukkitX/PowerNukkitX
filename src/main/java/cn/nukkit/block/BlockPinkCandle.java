package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(PINK_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockPinkCandleCake();
    }
}