package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonNylium extends BlockNylium {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_NYLIUM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonNylium() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonNylium(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Nylium";
    }
}