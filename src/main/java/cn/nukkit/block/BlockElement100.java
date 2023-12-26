package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement100 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_100");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement100() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement100(BlockState blockstate) {
        super(blockstate);
    }
}