package cn.nukkit.block;

import cn.nukkit.block.definition.BlockDefinitions;
import org.jetbrains.annotations.NotNull;

public class BlockNetherBrickFence extends BlockFence {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_BRICK_FENCE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherBrickFence() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrickFence(BlockState blockState) {
        super(blockState, BlockDefinitions.NETHER_BRICK_FENCE);
    }

    @Override
    public String getName() {
        return "Nether Brick Fence";
    }
}