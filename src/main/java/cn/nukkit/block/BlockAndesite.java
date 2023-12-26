package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockAndesite extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:andesite");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAndesite() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAndesite(BlockState blockstate) {
        super(blockstate);
    }
}