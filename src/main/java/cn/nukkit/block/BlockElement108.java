package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement108 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_108");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement108() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement108(BlockState blockstate) {
        super(blockstate);
    }
}