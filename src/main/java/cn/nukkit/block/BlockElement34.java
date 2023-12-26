package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement34 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_34");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement34() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement34(BlockState blockstate) {
        super(blockstate);
    }
}