package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHornCoral extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:horn_coral");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHornCoral() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHornCoral(BlockState blockstate) {
        super(blockstate);
    }
}