package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockDeadTubeCoral extends BlockTubeCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties(DEAD_TUBE_CORAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeadTubeCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeadTubeCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return true;
    }
}