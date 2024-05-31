package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(ORANGE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockOrangeCarpet(BlockState blockstate) {
        super(blockstate);
    }
}