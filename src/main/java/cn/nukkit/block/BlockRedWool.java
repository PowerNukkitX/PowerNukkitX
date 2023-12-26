package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockRedWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedWool(BlockState blockstate) {
        super(blockstate);
    }
}