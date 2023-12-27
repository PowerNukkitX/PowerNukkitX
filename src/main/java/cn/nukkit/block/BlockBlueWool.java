package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueWool(BlockState blockstate) {
        super(blockstate);
    }
}