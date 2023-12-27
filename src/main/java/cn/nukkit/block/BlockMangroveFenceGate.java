package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveFenceGate extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mangrove_fence_gate", CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangroveFenceGate(BlockState blockstate) {
        super(blockstate);
    }
}