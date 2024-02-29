package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockOrangeCandle();
    }
}