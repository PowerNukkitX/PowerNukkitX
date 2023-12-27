package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonFenceGate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:crimson_fence_gate", CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonFenceGate(BlockState blockstate) {
        super(blockstate);
    }
}