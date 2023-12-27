package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCherryFenceGate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_fence_gate", CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryFenceGate(BlockState blockstate) {
        super(blockstate);
    }
}