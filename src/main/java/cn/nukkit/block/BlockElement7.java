package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockElement7 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_7");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement7() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement7(BlockState blockstate) {
        super(blockstate);
    }
}