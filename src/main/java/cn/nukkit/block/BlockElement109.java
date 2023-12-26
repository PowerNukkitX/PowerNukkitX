package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement109 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_109");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement109() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement109(BlockState blockstate) {
        super(blockstate);
    }
}