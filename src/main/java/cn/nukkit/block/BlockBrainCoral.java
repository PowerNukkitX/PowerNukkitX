package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrainCoral extends BlockCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(BRAIN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrainCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrainCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public Block getDeadCoral() {
        return new BlockDeadBrainCoral();
    }
}