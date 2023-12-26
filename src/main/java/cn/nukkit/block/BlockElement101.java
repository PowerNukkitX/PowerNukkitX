package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement101 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_101");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement101() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement101(BlockState blockstate) {
        super(blockstate);
    }
}