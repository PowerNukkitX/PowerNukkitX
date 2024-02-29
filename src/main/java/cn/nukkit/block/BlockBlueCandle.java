package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlueCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockBlueCandleCake();
    }
}