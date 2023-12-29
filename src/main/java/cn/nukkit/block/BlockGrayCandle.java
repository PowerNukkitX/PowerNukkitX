package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockGrayCandleCake();
    }
}