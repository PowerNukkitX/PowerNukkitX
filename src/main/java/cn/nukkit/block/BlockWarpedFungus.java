package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedFungus extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_fungus");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFungus() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFungus(BlockState blockstate) {
        super(blockstate);
    }
}