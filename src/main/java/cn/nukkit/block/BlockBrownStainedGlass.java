package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}