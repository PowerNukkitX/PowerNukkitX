package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrackedNetherBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cracked_nether_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrackedNetherBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrackedNetherBricks(BlockState blockstate) {
        super(blockstate);
    }
}