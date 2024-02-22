package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMangrovePlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangrovePlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMangrovePlanks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Mangrove Planks";
    }
}