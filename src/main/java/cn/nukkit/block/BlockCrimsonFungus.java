package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFungus extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_fungus");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFungus() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFungus(BlockState blockstate) {
        super(blockstate);
    }
}