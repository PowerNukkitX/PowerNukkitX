package org.powernukkitx.block;

import org.jetbrains.annotations.NotNull;

public class BlockPoplarPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarPlanks(BlockState blockstate) {
        super(blockstate);
    }
}
