package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCherryPlanks extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cherry_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCherryPlanks(BlockState blockstate) {
        super(blockstate);
    }
}