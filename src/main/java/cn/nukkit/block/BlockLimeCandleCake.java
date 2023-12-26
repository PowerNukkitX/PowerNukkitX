package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}