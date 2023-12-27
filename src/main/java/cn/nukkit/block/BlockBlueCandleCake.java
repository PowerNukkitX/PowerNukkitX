package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlueCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_candle_cake", CommonBlockProperties.LIT);

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
}