package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlueCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockBlueCandle();
    }
}