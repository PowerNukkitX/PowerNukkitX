package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDarkOakFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(DARK_OAK_FENCE_GATE, CommonBlockProperties.DIRECTION, CommonBlockProperties.IN_WALL_BIT, CommonBlockProperties.OPEN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDarkOakFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDarkOakFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dark Oak Fence Gate";
    }
}