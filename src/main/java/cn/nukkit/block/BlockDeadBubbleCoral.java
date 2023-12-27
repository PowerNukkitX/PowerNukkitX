package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadBubbleCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dead_bubble_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadBubbleCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadBubbleCoral(BlockState blockstate) {
        super(blockstate);
    }
}