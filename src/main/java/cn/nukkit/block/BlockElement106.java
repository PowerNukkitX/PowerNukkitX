package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement106 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_106");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement106() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement106(BlockState blockstate) {
        super(blockstate);
    }
}