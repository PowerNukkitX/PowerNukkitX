package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement73 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_73");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement73() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement73(BlockState blockstate) {
        super(blockstate);
    }
}