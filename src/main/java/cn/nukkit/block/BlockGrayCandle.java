package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(GRAY_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockGrayCandleCake();
    }
}