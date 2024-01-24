package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadHornCoral extends BlockHornCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_HORN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadHornCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadHornCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}