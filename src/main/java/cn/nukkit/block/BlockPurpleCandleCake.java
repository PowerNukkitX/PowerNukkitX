package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}