package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueWool(BlockState blockstate) {
        super(blockstate);
    }
}