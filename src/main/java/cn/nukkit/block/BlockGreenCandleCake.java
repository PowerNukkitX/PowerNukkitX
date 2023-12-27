package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGreenCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}