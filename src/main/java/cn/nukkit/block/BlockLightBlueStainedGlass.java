package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueStainedGlass extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_stained_glass");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueStainedGlass(BlockState blockstate) {
        super(blockstate);
    }
}