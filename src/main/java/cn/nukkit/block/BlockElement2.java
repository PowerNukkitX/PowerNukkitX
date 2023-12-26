package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement2 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_2");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement2() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement2(BlockState blockstate) {
        super(blockstate);
    }
}