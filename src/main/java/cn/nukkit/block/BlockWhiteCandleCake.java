package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockWhiteCandle();
    }
}