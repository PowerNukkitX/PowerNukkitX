package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_HORN_CORAL);

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