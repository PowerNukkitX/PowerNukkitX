package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPinkCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(PINK_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPinkCarpet(BlockState blockstate) {
        super(blockstate);
    }
}