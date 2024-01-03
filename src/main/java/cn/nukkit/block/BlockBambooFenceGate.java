package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_FENCE_GATE, CommonBlockProperties.DIRECTION,CommonBlockProperties.IN_WALL_BIT,CommonBlockProperties.OPEN_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooFenceGate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooFenceGate(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Bamboo Fence Gate";
    }
}