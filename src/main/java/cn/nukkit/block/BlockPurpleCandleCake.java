package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
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