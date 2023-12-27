package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMudBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mud_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBricks(BlockState blockstate) {
        super(blockstate);
    }
}