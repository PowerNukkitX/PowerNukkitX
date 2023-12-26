package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement103 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_103");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement103() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement103(BlockState blockstate) {
        super(blockstate);
    }
}