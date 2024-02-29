package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockRedCandle();
    }
}