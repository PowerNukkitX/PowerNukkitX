package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockMagentaCandle();
    }
}