package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakFenceGate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:dark_oak_fence_gate", CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakFenceGate(BlockState blockstate) {
        super(blockstate);
    }
}