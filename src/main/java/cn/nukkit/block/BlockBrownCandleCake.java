package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}