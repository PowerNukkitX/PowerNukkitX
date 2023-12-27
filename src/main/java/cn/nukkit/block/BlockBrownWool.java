package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownWool(BlockState blockstate) {
        super(blockstate);
    }
}