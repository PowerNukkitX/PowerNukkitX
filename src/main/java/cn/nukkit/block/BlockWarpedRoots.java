package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockWarpedRoots extends BlockHanging implements BlockFlowerPot.FlowerPotBlock, Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_ROOTS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedRoots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Roots";
    }
}