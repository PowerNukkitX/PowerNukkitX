package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement59 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_59");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement59() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement59(BlockState blockstate) {
        super(blockstate);
    }
}