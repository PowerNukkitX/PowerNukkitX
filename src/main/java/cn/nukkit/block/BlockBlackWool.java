package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackWool extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_wool");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackWool() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackWool(BlockState blockstate) {
        super(blockstate);
    }
}