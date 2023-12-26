package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement10 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_10");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement10() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement10(BlockState blockstate) {
        super(blockstate);
    }
}