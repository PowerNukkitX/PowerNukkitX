package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement111 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_111");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement111() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement111(BlockState blockstate) {
        super(blockstate);
    }
}