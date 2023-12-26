package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement5 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_5");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement5() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement5(BlockState blockstate) {
        super(blockstate);
    }
}