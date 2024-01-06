package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedNylium extends BlockNylium {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_NYLIUM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedNylium() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedNylium(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Nylium";
    }
}