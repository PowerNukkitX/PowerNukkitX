package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadBubbleCoral extends BlockBubbleCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_BUBBLE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadBubbleCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadBubbleCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}