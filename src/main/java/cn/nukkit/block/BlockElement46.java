package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement46 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_46");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement46() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement46(BlockState blockstate) {
        super(blockstate);
    }
}