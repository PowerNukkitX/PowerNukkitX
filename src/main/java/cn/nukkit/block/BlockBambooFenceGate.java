package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBambooFenceGate extends BlockFenceGate {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:bamboo_fence_gate",
            CommonBlockProperties.DIRECTION,CommonBlockProperties.IN_WALL_BIT,CommonBlockProperties.OPEN_BIT);

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

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }
}