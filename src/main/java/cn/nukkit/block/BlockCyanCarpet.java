package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(CYAN_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCyanCarpet(BlockState blockstate) {
        super(blockstate);
    }
}