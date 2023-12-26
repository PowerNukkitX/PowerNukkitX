package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement48 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_48");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement48() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement48(BlockState blockstate) {
        super(blockstate);
    }
}