package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockYellowWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowWool(BlockState blockstate) {
        super(blockstate);
    }
}