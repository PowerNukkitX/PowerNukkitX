package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadFireCoral extends BlockFireCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_FIRE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadFireCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadFireCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}