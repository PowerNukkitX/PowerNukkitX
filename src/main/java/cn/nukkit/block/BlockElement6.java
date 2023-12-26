package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement6 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_6");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement6() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement6(BlockState blockstate) {
        super(blockstate);
    }
}