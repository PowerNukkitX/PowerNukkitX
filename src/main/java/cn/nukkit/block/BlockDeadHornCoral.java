package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dead_horn_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadHornCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadHornCoral(BlockState blockstate) {
        super(blockstate);
    }
}