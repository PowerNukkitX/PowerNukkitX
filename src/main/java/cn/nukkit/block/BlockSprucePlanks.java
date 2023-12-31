package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockSprucePlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:spruce_planks");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSprucePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSprucePlanks(BlockState blockstate) {
        super(blockstate);
    }
}