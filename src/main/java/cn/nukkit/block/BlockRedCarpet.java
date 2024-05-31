package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(RED_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockRedCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockRedCarpet(BlockState blockstate) {
        super(blockstate);
    }
}