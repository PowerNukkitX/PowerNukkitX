package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(PURPLE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockPurpleCarpet(BlockState blockstate) {
        super(blockstate);
    }
}