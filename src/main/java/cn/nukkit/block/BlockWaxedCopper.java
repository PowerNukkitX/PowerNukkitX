package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopper extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_copper");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopper() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopper(BlockState blockstate) {
        super(blockstate);
    }
}