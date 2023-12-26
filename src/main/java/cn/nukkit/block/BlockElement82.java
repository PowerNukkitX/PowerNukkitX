package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement82 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_82");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement82() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement82(BlockState blockstate) {
        super(blockstate);
    }
}