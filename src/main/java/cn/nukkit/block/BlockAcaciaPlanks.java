package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAcaciaPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(ACACIA_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAcaciaPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAcaciaPlanks(BlockState blockstate) {
        super(blockstate);
    }
}