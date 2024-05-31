package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockMagentaCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(MAGENTA_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockMagentaCarpet(BlockState blockstate) {
        super(blockstate);
    }
}