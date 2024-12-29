package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCrimsonRoots extends BlockHanging implements BlockFlowerPot.FlowerPotBlock, Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_ROOTS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonRoots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonRoots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Crimson Roots";
    }
}