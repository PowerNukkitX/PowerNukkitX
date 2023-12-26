package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement93 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_93");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement93() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement93(BlockState blockstate) {
        super(blockstate);
    }
}