package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement81 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_81");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement81() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement81(BlockState blockstate) {
        super(blockstate);
    }
}