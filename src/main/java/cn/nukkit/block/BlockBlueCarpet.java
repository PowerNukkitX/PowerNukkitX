package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlueCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(BLUE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueCarpet(BlockState blockstate) {
        super(blockstate);
    }
}