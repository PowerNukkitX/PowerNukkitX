package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleWool(BlockState blockstate) {
        super(blockstate);
    }
}