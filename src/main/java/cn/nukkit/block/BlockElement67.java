package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement67 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_67");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement67() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement67(BlockState blockstate) {
        super(blockstate);
    }
}