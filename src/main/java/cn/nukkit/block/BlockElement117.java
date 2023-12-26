package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement117 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_117");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement117() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement117(BlockState blockstate) {
        super(blockstate);
    }
}