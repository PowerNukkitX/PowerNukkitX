package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockFireCoral extends BlockCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(FIRE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFireCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFireCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public Block getDeadCoral() {
        return new BlockDeadFireCoral();
    }
}