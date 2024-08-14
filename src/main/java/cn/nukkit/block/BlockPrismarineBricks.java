package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPrismarineBricks extends BlockPrismarine {

    public static final BlockProperties PROPERTIES = new BlockProperties(PRISMARINE_BRICKS);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPrismarineBricks() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockPrismarineBricks(BlockState blockState) {
        super(blockState);
    }
}
