package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFungus extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_fungus");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFungus() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFungus(BlockState blockstate) {
        super(blockstate);
    }
}