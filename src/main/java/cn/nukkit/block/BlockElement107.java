package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement107 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_107");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement107() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement107(BlockState blockstate) {
        super(blockstate);
    }
}