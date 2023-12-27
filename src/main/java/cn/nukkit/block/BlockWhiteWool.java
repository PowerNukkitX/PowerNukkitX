package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWhiteWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteWool(BlockState blockstate) {
        super(blockstate);
    }
}