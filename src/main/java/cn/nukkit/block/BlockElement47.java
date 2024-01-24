package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement47 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_47");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement47() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement47(BlockState blockstate) {
        super(blockstate);
    }
}