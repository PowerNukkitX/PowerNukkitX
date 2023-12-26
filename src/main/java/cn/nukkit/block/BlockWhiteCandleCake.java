package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}