package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadBrainCoral extends BlockBrainCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BRAIN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadBrainCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadBrainCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}