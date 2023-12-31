package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockTubeCoral extends BlockCoral {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:tube_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTubeCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTubeCoral(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public Block getDeadCoral() {
        return new BlockDeadTubeCoral();
    }
}