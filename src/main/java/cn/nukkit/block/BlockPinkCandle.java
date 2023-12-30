package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockPinkCandleCake();
    }
}