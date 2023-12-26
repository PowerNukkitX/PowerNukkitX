package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement72 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_72");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement72() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement72(BlockState blockstate) {
        super(blockstate);
    }
}