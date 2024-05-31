package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockAcaciaPlanks extends BlockPlanks {
    public static final BlockProperties $1 = new BlockProperties(ACACIA_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockAcaciaPlanks() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockAcaciaPlanks(BlockState blockstate) {
        super(blockstate);
    }
}