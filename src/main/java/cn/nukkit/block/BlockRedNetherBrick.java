package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedNetherBrick extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_nether_brick");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedNetherBrick() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedNetherBrick(BlockState blockstate) {
        super(blockstate);
    }
}