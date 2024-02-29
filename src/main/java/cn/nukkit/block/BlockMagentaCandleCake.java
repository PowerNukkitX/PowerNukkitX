package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties(MAGENTA_CANDLE_CAKE, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockCandle toCandleForm() {
        return new BlockMagentaCandle();
    }
}