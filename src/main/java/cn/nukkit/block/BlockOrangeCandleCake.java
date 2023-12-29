package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeCandleCake extends BlockCandleCake {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeCandleCake(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected BlockCandle toCandleForm() {
        return new BlockOrangeCandle();
    }
}