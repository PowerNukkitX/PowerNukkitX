package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHardGlass extends Block {
    public static final BlockProperties $1 = new BlockProperties(HARD_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockHardGlass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockHardGlass(BlockState blockstate) {
        super(blockstate);
    }
}