package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}