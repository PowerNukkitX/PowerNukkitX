package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlueCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected Block toCakeForm() {
        return new BlockBlueCandleCake();
    }
}