package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockYellowCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}