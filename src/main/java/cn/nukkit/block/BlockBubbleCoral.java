package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBubbleCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:bubble_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBubbleCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBubbleCoral(BlockState blockstate) {
        super(blockstate);
    }
}