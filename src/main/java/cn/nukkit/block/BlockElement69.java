package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement69 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_69");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement69() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement69(BlockState blockstate) {
        super(blockstate);
    }
}