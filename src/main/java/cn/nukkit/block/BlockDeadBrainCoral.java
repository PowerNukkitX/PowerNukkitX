package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadBrainCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dead_brain_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadBrainCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadBrainCoral(BlockState blockstate) {
        super(blockstate);
    }
}