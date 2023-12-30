package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockRedCandleCake();
    }
}