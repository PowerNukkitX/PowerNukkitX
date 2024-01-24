package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHornCoral extends BlockCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(HORN_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHornCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHornCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public Block getDeadCoral() {
        return new BlockDeadHornCoral();
    }
}