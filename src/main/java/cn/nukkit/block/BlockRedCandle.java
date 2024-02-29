package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockRedCandleCake();
    }
}