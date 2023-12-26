package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement18 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_18");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement18() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement18(BlockState blockstate) {
        super(blockstate);
    }
}