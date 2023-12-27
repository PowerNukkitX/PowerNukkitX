package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedFenceGate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:warped_fence_gate", CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedFenceGate(BlockState blockstate) {
        super(blockstate);
    }
}