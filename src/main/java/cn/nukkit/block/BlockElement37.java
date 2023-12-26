package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement37 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_37");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement37() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement37(BlockState blockstate) {
        super(blockstate);
    }
}