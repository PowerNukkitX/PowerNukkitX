package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonNylium extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_nylium");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonNylium() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonNylium(BlockState blockstate) {
        super(blockstate);
    }
}