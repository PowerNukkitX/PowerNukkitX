package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockSprucePlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPRUCE_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSprucePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSprucePlanks(BlockState blockstate) {
        super(blockstate);
    }
}