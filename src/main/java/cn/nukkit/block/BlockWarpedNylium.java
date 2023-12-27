package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedNylium extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_nylium");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedNylium() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedNylium(BlockState blockstate) {
        super(blockstate);
    }
}