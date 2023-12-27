package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}