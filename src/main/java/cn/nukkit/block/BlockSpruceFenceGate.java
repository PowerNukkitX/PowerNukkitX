package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSpruceFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_FENCE_GATE, CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSpruceFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSpruceFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Spruce Fence Gate";
    }
}