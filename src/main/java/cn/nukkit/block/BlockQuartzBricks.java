package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockQuartzBricks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:quartz_bricks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockQuartzBricks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockQuartzBricks(BlockState blockstate) {
        super(blockstate);
    }
}